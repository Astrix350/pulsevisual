# ⚡ PulseVisual — Premium Visual Mod for Minecraft 1.21.1 (Fabric)

> Максимальное качество визуального опыта. Вдохновлён PulseVisual.

---

## ✨ Возможности

### 🖥 HUD
- **Custom Health Bar** — плавная анимированная полоса HP с цветом по проценту (зелёный → жёлтый → красный)
- **Armor Overlay** — полоса брони
- **Food Bar** — полоса сытости
- **XP Bar** — стильная полоса опыта
- **Custom Crosshair** — минималистичный прицел с поддержкой Chroma
- **Coordinates** — XYZ на экране
- **FPS Counter** — с цветовой индикацией (зелёный/жёлтый/красный)
- **Ping Display** — пинг в реальном времени
- **In-Game Time** — игровое время в формате HH:MM
- **Biome Display** — текущий биом
- **Direction Compass** — компас направления
- **Speed Display** — скорость движения в блоках/сек
- **Active Potions** — список активных зелий с таймерами
- **HUD Scale** — масштаб интерфейса от 0.5x до 2.0x

### 🎨 Visual Effects
- **Motion Blur** — размытие движения с настройкой силы
- **Damage Overlay** — красный виньет + вспышка при получении урона
- **Fullbright** — полная яркость (горячая клавиша F7)
- **Night Vision Tweak** — исправление мигания NV в конце
- **Chroma Mode** — радужная раскраска всего HUD (горячая клавиша F8)
- **Chroma Speed** — скорость цикла радуги
- **Low Fire** — низкий огонь (не перекрывает обзор)
- **No Pumpkin Blur** — убирает эффект тыквенного шлема
- **No Nausea Effect** — убирает шейдер тошноты
- **No Void Fog** — убирает туман пустоты
- **Item Physics** — физика выброшенных предметов (поворот/анимация)

### 📷 Camera
- **Zoom** — зум с плавным переходом (клавиша C)
- **Scroll to adjust zoom** — прокрутка колёсика при зуме меняет уровень
- **Smooth Zoom** — плавное приближение/отдаление
- **Zoom Level** — настройка максимального зума (1.5x — 16x)
- **Custom FOV** — ручная установка угла обзора
- **Sprint FOV Effect** — вкл/выкл эффект FOV при беге
- **View Bobbing** — качание камеры
- **Hand Sway Strength** — сила качания руки

### ⚙️ Gameplay
- **FPS Limiter** — ограничение FPS (15–300)

---

## 🎮 Горячие клавиши

| Клавиша | Действие |
|---------|----------|
| `C` | Зум (удерживать) |
| `Right Shift` | Открыть настройки PulseVisual |
| `F7` | Переключить Fullbright |
| `F8` | Переключить Chroma Mode |

*Все клавиши можно изменить в Настройки → Управление*

---

## 📦 Установка

1. Установи [Fabric Loader 0.16.7+](https://fabricmc.net/use/)
2. Установи [Fabric API 0.102.0+](https://modrinth.com/mod/fabric-api)
3. Скопируй `pulsevisual-2.0.0.jar` в папку `/mods`
4. Запусти Minecraft 1.21.1 с Fabric профилем

---

## 🔨 Сборка из исходников

```bash
# Требует: Java 21, Gradle 8.8+
./gradlew build
# JAR будет в build/libs/pulsevisual-2.0.0.jar
```

---

## 📁 Конфигурация

Файл конфига: `.minecraft/config/pulsevisual.json`  
Редактируется автоматически через меню (`Right Shift` в игре).

---

## ⚠️ Примечание

Этот мод **клиентский** — не требует установки на сервер.  
Совместим с OptiFabric, Sodium, Iris.

---

*PulseVisual v2.0.0 | Minecraft 1.21.1 | Fabric*
