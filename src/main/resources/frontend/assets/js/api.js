
const API_BASE_URL = 'http://localhost:8080';

console.log('API_BASE_URL установлен на:', API_BASE_URL);


async function registerUser(userData) {
  try {
    console.log('Отправка регистрации на:', `${API_BASE_URL}/user/register`);
    console.log('Данные:', userData);
    
    const response = await fetch(`${API_BASE_URL}/user/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: userData.username,
        password: userData.password,
        email: userData.email
      })
    });

    console.log('Ответ от сервера. Статус:', response.status);

    if (response.status === 201) {
      console.log('Пользователь успешно зарегистрирован');
      return { success: true, message: 'Регистрация успешна' };
    } else {
      const error = await response.text();
      console.error('Ошибка регистрации. Ответ:', error);
      return { success: false, message: error || 'Ошибка регистрации' };
    }
  } catch (error) {
    console.error('Ошибка подключения:', error);
    console.error('Сообщение ошибки:', error.message);
    return { success: false, message: 'Ошибка подключения к серверу: ' + error.message };
  }
}


async function loginUser(credentials) {
  try {
    console.log('Отправка логина на:', `${API_BASE_URL}/user/login`);
    console.log('Credentials:', credentials);
    
    const response = await fetch(`${API_BASE_URL}/user/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials)
    });

    console.log('Ответ от сервера. Статус:', response.status);

    if (response.ok) {
      const data = await response.json();
      console.log('Успешная авторизация. Данные:', data);
      
      // Сохраняем токен и данные пользователя
      if (data.token) {
        localStorage.setItem('authToken', data.token);
      }
      if (data.login) {
        localStorage.setItem('userLogin', data.login);
      }
      if (data.id) {
        localStorage.setItem('userId', data.id);
      }
      
      return { success: true, data: data };
    } else {
      const error = await response.text();
      console.error('Ошибка логина. Ответ:', error);
      return { success: false, message: error || 'Неверные учетные данные' };
    }
  } catch (error) {
    console.error('Ошибка подключения при логине:', error);
    console.error('Сообщение ошибки:', error.message);
    return { success: false, message: 'Ошибка подключения к серверу: ' + error.message };
  }
}

// ПОЛУЧЕНИЕ ПРОФИЛЯ ПОЛЬЗОВАТЕЛЯ
async function getUserProfile() {
  try {
    const token = localStorage.getItem('authToken');
    
    const response = await fetch(`${API_BASE_URL}/user/profile`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      // Токен истек или невалиден
      localStorage.removeItem('authToken');
      localStorage.removeItem('userLogin');
      localStorage.removeItem('userId');
      return { success: false, message: 'Сессия истекла. Пожалуйста, авторизуйтесь снова' };
    } else {
      return { success: false, message: 'Ошибка получения профиля' };
    }
  } catch (error) {
    console.error('Ошибка получения профиля:', error);
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}

// ПОЛУЧЕНИЕ ЗАКАЗОВ ПОЛЬЗОВАТЕЛЯ
async function getUserOrders() {
  try {
    const token = localStorage.getItem('authToken');
    
    const response = await fetch(`${API_BASE_URL}/user/orders`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('userLogin');
      localStorage.removeItem('userId');
      return { success: false, message: 'Сессия истекла' };
    } else {
      return { success: false, message: 'Ошибка получения заказов' };
    }
  } catch (error) {
    console.error('Ошибка получения заказов:', error);
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}

// СМЕНА ПАРОЛЯ
async function changePassword(passwordData) {
  try {
    const token = localStorage.getItem('authToken');

    const response = await fetch(`${API_BASE_URL}/user/change-password`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(passwordData)
    });

    if (response.ok) {
      return { success: true, message: 'Пароль успешно изменен' };
    } else if (response.status === 401) {
      localStorage.removeItem('authToken');
      return { success: false, message: 'Сессия истекла' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка смены пароля' };
    }
  } catch (error) {
    console.error('Ошибка смены пароля:', error);
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}


// ВЫХОД ИЗ АККАУНТА
function logout() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('userLogin');
  localStorage.removeItem('userId');
  console.log('Вы вышли из аккаунта');
  window.location.href = '/';
}


// ПРОВЕРКА АВТОРИЗАЦИИ
function isAuthenticated() {
  return localStorage.getItem('authToken') !== null;
}


// ПОЛУЧЕНИЕ ТОКЕНА
function getAuthToken() {
  return localStorage.getItem('authToken');
}


// ДОБАВИТЬ ТОВАР В КОРЗИНУ
async function addToCart(itemData) {
  try {
    const token = localStorage.getItem('authToken');
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 5000);

    const response = await fetch(`${API_BASE_URL}/cart/add`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(itemData),
      signal: controller.signal
    });

    clearTimeout(timeout);

    if (response.ok) {
      return { success: true };
    } else if (response.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('userLogin');
      localStorage.removeItem('userId');
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

// ПОЛУЧИТЬ СОДЕРЖИМОЕ КОРЗИНЫ
async function getCartItems() {
  try {
    const token = localStorage.getItem('authToken');
    const response = await fetch(`${API_BASE_URL}/cart/items`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      localStorage.removeItem('authToken');
      return { success: false, message: 'Сессия истекла' };
    } else {
      return { success: false, message: 'Ошибка получения корзины' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения' };
  }
}

// УДАЛИТЬ ТОВАР ИЗ КОРЗИНЫ
async function removeFromCart(itemId) {
  try {
    const token = localStorage.getItem('authToken');
    const response = await fetch(`${API_BASE_URL}/cart/items/${itemId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });

    if (response.ok) return { success: true };
    return { success: false, message: 'Ошибка удаления из корзины' };
  } catch (error) {
    return { success: false, message: 'Ошибка подключения' };
  }
}

// ОФОРМИТЬ ЗАКАЗ (CHECKOUT)
async function checkout(paymentMethod) {
  try {
    const token = localStorage.getItem('authToken');
    const response = await fetch(`${API_BASE_URL}/order/checkout`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ paymentMethod })
    });

    if (response.ok) {
      const data = await response.json();
      return { success: true, data: data };
    } else if (response.status === 401) {
      localStorage.removeItem('authToken');
      return { success: false, message: 'Сессия истекла' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка оформления заказа' };
    }
  } catch (error) {
    return { success: false, message: 'Ошибка подключения' };
  }
}


// ОБНОВЛЕНИЕ ДАННЫХ ПОЛЬЗОВАТЕЛЯ
async function changeUserData(userData) {
  try {
    const token = localStorage.getItem('authToken');

    const response = await fetch(`${API_BASE_URL}/user/change-data`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(userData)
    });

    if (response.ok) {
      const data = await response.json();
      if (data.username) localStorage.setItem('userLogin', data.username);
      return { success: true, data: data };
    } else if (response.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('userLogin');
      localStorage.removeItem('userId');
      return { success: false, message: 'Сессия истекла' };
    } else {
      const error = await response.text();
      return { success: false, message: error || 'Ошибка обновления данных' };
    }
  } catch (error) {
    console.error('Ошибка обновления данных:', error);
    return { success: false, message: 'Ошибка подключения к серверу' };
  }
}