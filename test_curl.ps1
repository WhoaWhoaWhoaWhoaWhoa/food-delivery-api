Write-Host "=== Testing Food Delivery API - Complete User Tests ===" -ForegroundColor Cyan
Write-Host "Starting at: $(Get-Date)" -ForegroundColor Cyan
Write-Host ""

# 1. Создаем разных пользователей
Write-Host "1. Creating test users..." -ForegroundColor Yellow

# Создаем customer
$customerBody = '{"username":"testcustomer","email":"customer@test.com","password":"password123","phone":"+79991234567","role":"CUSTOMER"}'
try {
    $customer = Invoke-RestMethod -Uri "http://localhost:8080/users" -Method Post -ContentType "application/json" -Body $customerBody
    Write-Host "[OK] Customer created: ID=$($customer.id), Username=$($customer.username), Role=$($customer.role)" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Error creating customer: $($_.Exception.Message)" -ForegroundColor Red
}

# Создаем admin
$adminBody = '{"username":"testadmin","email":"admin@test.com","password":"password123","phone":"+79997654321","role":"ADMIN"}'
try {
    $admin = Invoke-RestMethod -Uri "http://localhost:8080/users" -Method Post -ContentType "application/json" -Body $adminBody
    Write-Host "[OK] Admin created: ID=$($admin.id), Username=$($admin.username), Role=$($admin.role)" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Error creating admin: $($_.Exception.Message)" -ForegroundColor Red
}

# Создаем courier
$courierBody = '{"username":"testcourier","email":"courier@test.com","password":"password123","phone":"+79995556677","role":"COURIER"}'
try {
    $courier = Invoke-RestMethod -Uri "http://localhost:8080/users" -Method Post -ContentType "application/json" -Body $courierBody
    Write-Host "[OK] Courier created: ID=$($courier.id), Username=$($courier.username), Role=$($courier.role)" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Error creating courier: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 2. Тестируем GET всех пользователей
Write-Host "2. Testing GET /users (all users)..." -ForegroundColor Yellow
try {
    $allUsers = Invoke-RestMethod -Uri "http://localhost:8080/users" -Method Get
    Write-Host "[OK] Total users: $($allUsers.Count)" -ForegroundColor Green
    foreach ($user in $allUsers) {
        Write-Host "  - ID: $($user.id), Username: $($user.username), Role: $($user.role), Active: $($user.active)" -ForegroundColor Gray
    }
} catch {
    Write-Host "[ERROR] Error getting all users: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 3. Тестируем фильтрацию по роли
Write-Host "3. Testing GET /users?role=CUSTOMER..." -ForegroundColor Yellow
try {
    $customers = Invoke-RestMethod -Uri "http://localhost:8080/users?role=CUSTOMER" -Method Get
    Write-Host "[OK] Found $($customers.Count) customer(s)" -ForegroundColor Green
    foreach ($cust in $customers) {
        Write-Host "  - $($cust.username) (ID: $($cust.id))" -ForegroundColor Gray
    }
} catch {
    Write-Host "[ERROR] Error filtering by role: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 4. Тестируем GET конкретного пользователя
Write-Host "4. Testing GET /users/{id}..." -ForegroundColor Yellow
if ($customer -and $customer.id) {
    try {
        $singleUser = Invoke-RestMethod -Uri "http://localhost:8080/users/$($customer.id)" -Method Get
        Write-Host "[OK] User details:" -ForegroundColor Green
        Write-Host "  ID: $($singleUser.id)" -ForegroundColor Gray
        Write-Host "  Username: $($singleUser.username)" -ForegroundColor Gray
        Write-Host "  Email: $($singleUser.email)" -ForegroundColor Gray
        Write-Host "  Role: $($singleUser.role)" -ForegroundColor Gray
        Write-Host "  Active: $($singleUser.active)" -ForegroundColor Gray
        Write-Host "  Created: $($singleUser.createdAt)" -ForegroundColor Gray
        Write-Host "  Updated: $($singleUser.updatedAt)" -ForegroundColor Gray
    } catch {
        Write-Host "[ERROR] Error getting user: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "[SKIP] No customer ID available for testing" -ForegroundColor Yellow
}

Write-Host ""

# 5. Тестируем PUT (обновление пользователя)
Write-Host "5. Testing PUT /users/{id}..." -ForegroundColor Yellow
if ($customer -and $customer.id) {
    $updateBody = '{"username":"updatedcustomer","email":"updated.customer@test.com","phone":"+79998887766","active":true}'
    try {
        $updatedUser = Invoke-RestMethod -Uri "http://localhost:8080/users/$($customer.id)" -Method Put -ContentType "application/json" -Body $updateBody
        Write-Host "[OK] User updated successfully:" -ForegroundColor Green
        Write-Host "  Old username: $($customer.username)" -ForegroundColor Gray
        Write-Host "  New username: $($updatedUser.username)" -ForegroundColor Green
        Write-Host "  Old email: $($customer.email)" -ForegroundColor Gray
        Write-Host "  New email: $($updatedUser.email)" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Error updating user: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "[SKIP] No customer ID available for update" -ForegroundColor Yellow
}

Write-Host ""

# 6. Тестируем DELETE
Write-Host "6. Testing DELETE /users/{id} (deactivate)..." -ForegroundColor Yellow
if ($customer -and $customer.id) {
    try {
        $deactivatedUser = Invoke-RestMethod -Uri "http://localhost:8080/users/$($customer.id)" -Method Delete
        Write-Host "[OK] User deactivated:" -ForegroundColor Green
        if ($deactivatedUser.active -eq $false) {
            Write-Host "  Active status: $($deactivatedUser.active) (correct: false)" -ForegroundColor Green
        } else {
            Write-Host "  Active status: $($deactivatedUser.active) (incorrect: should be false)" -ForegroundColor Red
        }
    } catch {
        Write-Host "[ERROR] Error deactivating user: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "[SKIP] No customer ID available for deactivation" -ForegroundColor Yellow
}

Write-Host ""

# 7. Тестируем PATCH
Write-Host "7. Testing PATCH /users/{id}/activate..." -ForegroundColor Yellow
if ($customer -and $customer.id) {
    try {
        $activatedUser = Invoke-RestMethod -Uri "http://localhost:8080/users/$($customer.id)/activate" -Method Patch
        Write-Host "[OK] User activated:" -ForegroundColor Green
        if ($activatedUser.active -eq $true) {
            Write-Host "  Active status: $($activatedUser.active) (correct: true)" -ForegroundColor Green
        } else {
            Write-Host "  Active status: $($activatedUser.active) (incorrect: should be true)" -ForegroundColor Red
        }
    } catch {
        Write-Host "[ERROR] Error activating user: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "[SKIP] No customer ID available for activation" -ForegroundColor Yellow
}

Write-Host ""

# 8. Тестируем фильтрацию после изменений
Write-Host "8. Testing GET /users?role=CUSTOMER after updates..." -ForegroundColor Yellow
try {
    $customersAfter = Invoke-RestMethod -Uri "http://localhost:8080/users?role=CUSTOMER" -Method Get
    if ($customer -and $customer.id) {
        $updatedCustomer = $customersAfter | Where-Object { $_.id -eq $customer.id }
        if ($updatedCustomer) {
            Write-Host "[OK] Updated customer found:" -ForegroundColor Green
            Write-Host "  Username: $($updatedCustomer.username)" -ForegroundColor Gray
            Write-Host "  Email: $($updatedCustomer.email)" -ForegroundColor Gray
            Write-Host "  Active: $($updatedCustomer.active)" -ForegroundColor Gray
        } else {
            Write-Host "[WARNING] Updated customer not found in filtered list" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "[ERROR] Error filtering after updates: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 9. Тестируем проверку уникальности
Write-Host "9. Testing duplicate user creation (should fail)..." -ForegroundColor Yellow
try {
    $duplicateResponse = Invoke-RestMethod -Uri "http://localhost:8080/users" -Method Post -ContentType "application/json" -Body $customerBody -ErrorAction Stop
    Write-Host "[ERROR] UNEXPECTED: Duplicate user created (should have failed!)" -ForegroundColor Red
} catch {
    Write-Host "[OK] Expected error for duplicate: $($_.Exception.Message)" -ForegroundColor Green
}

Write-Host ""

# 10. Тестируем валидацию
Write-Host "10. Testing validation errors..." -ForegroundColor Yellow
$invalidBody = '{"username":"ab","email":"invalid-email","password":"123"}'
try {
    $invalidResponse = Invoke-RestMethod -Uri "http://localhost:8080/users" -Method Post -ContentType "application/json" -Body $invalidBody -ErrorAction Stop
    Write-Host "[ERROR] UNEXPECTED: Invalid user created (should have failed!)" -ForegroundColor Red
} catch {
    Write-Host "[OK] Expected validation error: $($_.Exception.Message)" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Summary ===" -ForegroundColor Cyan
Write-Host "Test completed at: $(Get-Date)" -ForegroundColor Cyan
Write-Host ""

# Финальная проверка всех пользователей
try {
    $finalUsers = Invoke-RestMethod -Uri "http://localhost:8080/users" -Method Get
    Write-Host "Final user count: $($finalUsers.Count)" -ForegroundColor Cyan
    
    $activeUsers = $finalUsers | Where-Object { $_.active -eq $true }
    $inactiveUsers = $finalUsers | Where-Object { $_.active -eq $false }
    
    Write-Host "Active users: $($activeUsers.Count)" -ForegroundColor Green
    Write-Host "Inactive users: $($inactiveUsers.Count)" -ForegroundColor Yellow
    
    # Группировка по ролям
    $roleGroups = $finalUsers | Group-Object -Property role
    Write-Host "`nUsers by role:" -ForegroundColor Cyan
    foreach ($group in $roleGroups) {
        Write-Host "  $($group.Name): $($group.Count) user(s)" -ForegroundColor Gray
    }
} catch {
    Write-Host "[ERROR] Error getting final user count: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Testing completed ===" -ForegroundColor Cyan