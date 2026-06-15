const API_BASE_URL = 'http://localhost:8080';

(function() {
  const t = localStorage.getItem('authToken');
  if (t && !sessionStorage.getItem('authToken')) {
    sessionStorage.setItem('authToken', t);
    ['userLogin','userEmail','userId'].forEach(k => {
      const v = localStorage.getItem(k);
      if (v) sessionStorage.setItem(k, v);
      localStorage.removeItem(k);
    });
    localStorage.removeItem('authToken');
  }
})();

async function registerUser(userData) {
  try {

    const response = await fetch(`${API_BASE_URL}/user/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify({
        name:     userData.name     || userData.username,
        username: userData.login    || userData.username,
        password: userData.password
      })
    });

    if (response.status === 201) {
      return { success: true, message: 'Регистрация успешна' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка регистрации' };
    }
  } catch (error) {

    return { success: false, message: 'Ошибка подключения к серверу: ' + error.message };
  }
}

async function loginUser(credentials) {
  try {

    const response = await fetch(`${API_BASE_URL}/user/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify(credentials)
    });

    if (response.ok) {
      const data = await response.json();
      if (data.token) {
        sessionStorage.setItem('authToken', data.token);
      }
      if (data.username) {
        sessionStorage.setItem('userLogin', data.username);
      }
      if (data.email) {
        sessionStorage.setItem('userEmail', data.email);
      }

      return { success: true, data: data };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Неверные учетные данные' };
    }
  } catch (error) {

    return { success: false, message: 'Ошибка подключения к серверу: ' + error.message };
  }
}
async function getUserProfile() {
  try {
    const token = sessionStorage.getItem('authToken');

    const response = await fetch(`${API_BASE_URL}/user/profile`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('userLogin');
      sessionStorage.removeItem('userId');
      return { success: false, message: 'Сессия истекла. Пожалуйста, авторизуйтесь снова' };
    } else {
      return { success: false, message: 'Ошибка получения профиля' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}
async function getUserOrders() {
  try {
    const token = sessionStorage.getItem('authToken');

    const response = await fetch(`${API_BASE_URL}/user/orders`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('userLogin');
      sessionStorage.removeItem('userId');
      return { success: false, message: 'Сессия истекла' };
    } else {
      return { success: false, message: 'Ошибка получения заказов' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}
async function changePassword(passwordData) {
  try {
    const token = sessionStorage.getItem('authToken');

    const response = await fetch(`${API_BASE_URL}/user/change-password`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(passwordData)
    });

    if (response.ok) {
      return { success: true, message: 'Пароль успешно изменен' };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      return { success: false, message: 'Сессия истекла' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка смены пароля' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}
function logout() {
  sessionStorage.removeItem('authToken');
  sessionStorage.removeItem('userLogin');
  sessionStorage.removeItem('userId');
  window.location.href = '/';
}
function isAuthenticated() {
  return sessionStorage.getItem('authToken') !== null;
}
function getAuthToken() {
  return sessionStorage.getItem('authToken');
}
async function addToCart(itemData) {
  try {
    const token = sessionStorage.getItem('authToken');
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 5000);

    const response = await fetch(`${API_BASE_URL}/cart/add`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(itemData),
      signal: controller.signal
    });

    clearTimeout(timeout);

    if (response.ok) {
      return { success: true };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('userLogin');
      sessionStorage.removeItem('userId');
      return { success: false, message: 'Сессия истекла' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка добавления в корзину' };
    }
  } catch (error) {
    if (error.name === 'AbortError') return { success: false, message: 'Сервер недоступен' };
    return { success: false, message: 'Ошибка подключения' };
  }
}
async function getCartItems() {
  try {
    const token = sessionStorage.getItem('authToken');
    const response = await fetch(`${API_BASE_URL}/cart/items`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      return { success: false, message: 'Сессия истекла' };
    } else {
      return { success: false, message: 'Ошибка получения корзины' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения' };
  }
}
async function removeFromCart(itemId) {
  try {
    const token = sessionStorage.getItem('authToken');
    const response = await fetch(`${API_BASE_URL}/cart/items/${itemId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) return { success: true };
    return { success: false, message: 'Ошибка удаления из корзины' };
  } catch (error) {
    return { success: false, message: 'Ошибка подключения' };
  }
}
async function checkout(checkoutData) {
  try {
    const token = sessionStorage.getItem('authToken');
    const payload = typeof checkoutData === 'string'
      ? { paymentMethod: checkoutData }
      : {
          customerUsername: checkoutData.contactName   || '',
          customerEmail:    checkoutData.contactEmail  || '',
          customerPhone:    checkoutData.contactPhone  || '',
          paymentMethod:    checkoutData.paymentMethod || 'CASH'
        };

    const response = await fetch(`${API_BASE_URL}/order/checkout`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      return { success: false, message: 'Сессия истекла' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка оформления заказа' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения' };
  }
}
async function changeUserData(userData) {
  try {
    const token = sessionStorage.getItem('authToken');

    const response = await fetch(`${API_BASE_URL}/user/change-data`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(userData)
    });

    if (response.ok) {
      const data = await response.json();
      if (data.username) sessionStorage.setItem('userLogin', data.username);
      return { success: true, data: data };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('userLogin');
      sessionStorage.removeItem('userId');
      return { success: false, message: 'Сессия истекла' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка обновления данных' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}

async function getOrderDetails(orderId) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/user/orders/${orderId}`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
    if (response.ok) {
      return { success: true, data: await response.json() };
    } else if (response.status === 401) {
      sessionStorage.removeItem('authToken');
      return { success: false, message: 'Сессия истекла' };
    } else {
      return { success: false, message: 'Ошибка загрузки заказа' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения' };
  }
}

async function getAdminUsers() {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/users`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
    if (response.ok) return { success: true, data: await response.json() };
    return { success: false, status: response.status };
  } catch (e) {
    return { success: false, message: 'Ошибка подключения' };
  }
}

async function getAdminUser(userId) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/users/${userId}`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
    if (response.ok) return { success: true, data: await response.json() };
    return { success: false, status: response.status };
  } catch (e) {
    return { success: false };
  }
}

async function updateAdminNote(userId, note) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/users/${userId}/note`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ adminNote: note })
    });
    return response.ok ? { success: true } : { success: false };
  } catch (e) {
    return { success: false };
  }
}

async function getAdminOrders() {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/orders`, {
      headers: {
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
    if (response.ok) return { success: true, data: await response.json() };
    return { success: false, status: response.status };
  } catch (e) {
    return { success: false, message: 'Ошибка подключения' };
  }
}

async function updateAdminOrder(orderId, data) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/orders/${orderId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(data)
    });
    if (response.ok) return { success: true, data: await response.json() };
    return { success: false, status: response.status };
  } catch (e) {
    return { success: false };
  }
}

async function getAdminOrderDetails(orderId) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/orders/${orderId}`, {
      headers: { 'Accept': 'application/json', 'Authorization': `Bearer ${token}` }
    });
    if (response.ok) return { success: true, data: await response.json() };
    return { success: false, status: response.status };
  } catch (e) { return { success: false }; }
}

async function updateAdminUser(userId, data) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/users/${userId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify(data)
    });
    if (response.ok) return { success: true, data: await response.json() };
    return { success: false };
  } catch (e) { return { success: false }; }
}

async function deleteAdminUser(userId) {
  try {
    const token = getAuthToken();
    const response = await fetch(`${API_BASE_URL}/admin/users/${userId}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    return { success: response.ok || response.status === 204 };
  } catch (e) { return { success: false }; }
}