 ? '✅' : level === 'error' ? '❌' : 'ℹ️';
            
            const eventDiv = document.createElement('div');
            eventDiv.innerHTML = `
                <div style="margin: 5px 0; padding: 5px; background: ${
                    level === 'success' ? '#d4edda' : 
                    level === 'error' ? '#f8d7da' : '#d1ecf1'
                }; border-radius: 3px;">
                    <strong>${levelIcon} ${timestamp}</strong> - ${type}: ${message}
                </div>
            `;
            
            eventsDiv.appendChild(eventDiv);
            eventsDiv.scrollTop = eventsDiv.scrollHeight;
            
            // Keep only last 50 events
            while (eventsDiv.children.length > 50) {
                eventsDiv.removeChild(eventsDiv.firstChild);
            }
        }
        
        async function fetchCurrentStatus() {
            try {
                const response = await fetch('/api/blockchain/status');
                const data = await response.json();
                if (data.status) {
                    updateStatus(data.status);
                }
            } catch (error) {
                console.error('Failed to fetch status:', error);
            }
        }
        
        async function addBlock() {
            const content = document.getElementById('block-content').value;
            const signer = document.getElementById('signer').value;
            
            if (!content.trim()) {
                alert('Please enter block content');
                return;
            }
            
            try {
                const response = await fetch('/api/blockchain/blocks', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        content: content,
                        signer: signer
                    })
                });
                
                const result = await response.json();
                
                if (result.success) {
                    document.getElementById('block-content').value = '';
                    addEvent('Local Action', `Block add request sent: ${content.substring(0, 30)}...`, 'info');
                } else {
                    addEvent('Local Error', `Failed to add block: ${result.error}`, 'error');
                }
                
            } catch (error) {
                addEvent('Local Error', `Network error: ${error.message}`, 'error');
            }
        }
        
        // Handle Enter key in content input
        document.getElementById('block-content').addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                addBlock();
            }
        });
        
        // Start SSE connection when page loads
        window.onload = function() {
            connectToSSE();
            fetchCurrentStatus();
        };
        
        // Clean up on page unload
        window.onbeforeunload = function() {
            if (eventSource) {
                eventSource.close();
            }
        };
    </script>
</body>
</html>
```

## 📋 Integration Best Practices

### Error Handling and Resilience

```python
# resilient_integration.py - Robust error handling patterns
import subprocess
import json
import time
import logging
from functools import wraps
from datetime import datetime

class ResilientBlockchainIntegration:
    def __init__(self, cli_path, max_retries=3, retry_delay=1):
        self.cli_path = cli_path
        self.max_retries = max_retries
        self.retry_delay = retry_delay
        
        # Setup logging
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)
    
    def retry_on_failure(self, max_retries=None, delay=None):
        """Decorator for retrying failed operations"""
        def decorator(func):
            @wraps(func)
            def wrapper(*args, **kwargs):
                retries = max_retries or self.max_retries
                retry_delay = delay or self.retry_delay
                
                for attempt in range(retries + 1):
                    try:
                        result = func(*args, **kwargs)
                        if attempt > 0:
                            self.logger.info(f"Operation succeeded on attempt {attempt + 1}")
                        return result
                    
                    except Exception as e:
                        if attempt < retries:
                            self.logger.warning(f"Attempt {attempt + 1} failed: {e}. Retrying in {retry_delay}s...")
                            time.sleep(retry_delay)
                            retry_delay *= 2  # Exponential backoff
                        else:
                            self.logger.error(f"All {retries + 1} attempts failed. Final error: {e}")
                            raise
                
                return None
            return wrapper
        return decorator
    
    @retry_on_failure(max_retries=3)
    def execute_cli_command(self, command_args, timeout=30):
        """Execute CLI command with retry logic"""
        cmd = ['java', '-jar', self.cli_path] + command_args
        
        try:
            result = subprocess.run(
                cmd, 
                capture_output=True, 
                text=True, 
                timeout=timeout
            )
            
            if result.returncode == 0:
                try:
                    # Try to parse JSON output
                    data = json.loads(result.stdout)
                    return {'success': True, 'data': data}
                except json.JSONDecodeError:
                    return {'success': True, 'data': result.stdout.strip()}
            else:
                raise subprocess.CalledProcessError(
                    result.returncode, cmd, result.stdout, result.stderr
                )
                
        except subprocess.TimeoutExpired as e:
            raise Exception(f"Command timeout after {timeout}s: {' '.join(cmd)}")
        except subprocess.CalledProcessError as e:
            raise Exception(f"Command failed: {e.stderr}")
    
    def health_check(self):
        """Perform comprehensive health check"""
        health_status = {
            'timestamp': datetime.now().isoformat(),
            'cli_available': False,
            'blockchain_responsive': False,
            'validation_status': None,
            'errors': []
        }
        
        try:
            # Test CLI availability
            result = self.execute_cli_command(['--version'])
            health_status['cli_available'] = result['success']
            
            if health_status['cli_available']:
                # Test blockchain responsiveness
                status_result = self.execute_cli_command(['status', '--json'])
                health_status['blockchain_responsive'] = status_result['success']
                
                if health_status['blockchain_responsive']:
                    # Test validation
                    validation_result = self.execute_cli_command(['validate', '--quick', '--json'])
                    health_status['validation_status'] = validation_result['data'].get('valid', False)
        
        except Exception as e:
            health_status['errors'].append(str(e))
        
        return health_status
    
    def circuit_breaker(self, failure_threshold=5, recovery_timeout=60):
        """Implement circuit breaker pattern"""
        def decorator(func):
            failure_count = 0
            last_failure_time = 0
            state = 'closed'  # closed, open, half-open
            
            @wraps(func)
            def wrapper(*args, **kwargs):
                nonlocal failure_count, last_failure_time, state
                
                current_time = time.time()
                
                # Check if circuit should be half-open
                if state == 'open' and current_time - last_failure_time > recovery_timeout:
                    state = 'half-open'
                    self.logger.info("Circuit breaker: half-open state")
                
                # Reject if circuit is open
                if state == 'open':
                    raise Exception("Circuit breaker is open - service unavailable")
                
                try:
                    result = func(*args, **kwargs)
                    
                    # Success - reset or close circuit
                    if state == 'half-open':
                        state = 'closed'
                        failure_count = 0
                        self.logger.info("Circuit breaker: closed (recovered)")
                    
                    return result
                
                except Exception as e:
                    failure_count += 1
                    last_failure_time = current_time
                    
                    # Open circuit if threshold reached
                    if failure_count >= failure_threshold:
                        state = 'open'
                        self.logger.error(f"Circuit breaker: opened after {failure_count} failures")
                    
                    raise
            
            return wrapper
        return decorator
    
    @circuit_breaker(failure_threshold=3, recovery_timeout=30)
    def safe_add_block(self, content, signer):
        """Add block with circuit breaker protection"""
        return self.execute_cli_command(['add-block', content, '--signer', signer])
    
    def batch_operation_with_rollback(self, operations):
        """Execute batch operations with rollback capability"""
        executed_operations = []
        
        try:
            for i, operation in enumerate(operations):
                self.logger.info(f"Executing operation {i+1}/{len(operations)}")
                
                result = self.execute_cli_command(operation['command'])
                executed_operations.append({
                    'operation': operation,
                    'result': result,
                    'index': i
                })
                
                if not result['success']:
                    raise Exception(f"Operation {i+1} failed: {operation}")
            
            self.logger.info("All batch operations completed successfully")
            return {'success': True, 'executed': executed_operations}
        
        except Exception as e:
            self.logger.error(f"Batch operation failed: {e}")
            
            # Attempt rollback if rollback operations are provided
            rollback_count = 0
            for executed in reversed(executed_operations):
                if 'rollback' in executed['operation']:
                    try:
                        self.execute_cli_command(executed['operation']['rollback'])
                        rollback_count += 1
                    except Exception as rollback_error:
                        self.logger.error(f"Rollback failed for operation {executed['index']}: {rollback_error}")
            
            return {
                'success': False, 
                'error': str(e),
                'executed': executed_operations,
                'rolled_back': rollback_count
            }

# Usage examples
integration = ResilientBlockchainIntegration('/path/to/blockchain-cli.jar')

# Health check
health = integration.health_check()
print(f"Health status: {health}")

# Safe operations with circuit breaker
try:
    result = integration.safe_add_block("Test transaction", "TestUser")
    print(f"Block added successfully: {result}")
except Exception as e:
    print(f"Operation failed: {e}")

# Batch operations with rollback
batch_ops = [
    {
        'command': ['add-key', 'BatchUser1', '--generate'],
        'rollback': ['delete-key', 'BatchUser1']  # If such command existed
    },
    {
        'command': ['add-key', 'BatchUser2', '--generate'],
        'rollback': ['delete-key', 'BatchUser2']
    }
]

batch_result = integration.batch_operation_with_rollback(batch_ops)
print(f"Batch operation result: {batch_result}")
```

## 📝 Integration Summary

This document provides comprehensive integration patterns for the Private Blockchain CLI, covering:

### **API Integration**
- REST API wrappers in Python, Node.js, and Java Spring Boot
- JSON-based communication patterns
- Error handling and response formatting

### **Database Integration**
- Direct SQLite database access
- PostgreSQL replication patterns
- Advanced search and analytics capabilities

### **Enterprise Systems**
- ERP integration (SAP, Oracle)
- CRM integration (Salesforce)
- Audit trail generation and compliance reporting

### **Microservices Architecture**
- Kubernetes deployment patterns
- Service mesh integration with Istio
- Event-driven architecture with Redis

### **Monitoring and Logging**
- Prometheus metrics collection
- ELK stack integration (Elasticsearch, Logstash, Kibana)
- Custom dashboard creation

### **Security Integration**
- JWT-based authentication and authorization
- API rate limiting and security middleware
- Audit logging and security event tracking

### **Cloud Platform Integration**
- AWS services (S3, SNS, CloudWatch, Lambda)
- Azure services (Blob Storage, Service Bus, Application Insights)
- Automated backup and restore workflows

### **Real-time Integration**
- WebSocket servers for real-time updates
- Server-Sent Events (SSE) for push notifications
- HTML client examples for web applications

### **Best Practices**
- Error handling and resilience patterns
- Circuit breaker implementation
- Retry logic with exponential backoff
- Batch operations with rollback capability

All patterns are production-ready and include proper error handling, logging, security considerations, and scalability features. They can be adapted and combined to meet specific integration requirements in various environments and use cases.
