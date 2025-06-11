# System Rezerwacji Sal

Nowoczesna aplikacja mobilna na platformę Android umożliwiająca efektywne zarządzanie rezerwacjami sal w budynkach uczelni, firm czy instytucji. Aplikacja została zaprojektowana z myślą o intuicyjności użytkowania oraz kompletności funkcjonalności.

## Kluczowe Funkcjonalności

### Zarządzanie Użytkownikami
- **Bezpieczna autoryzacja**: Rejestracja i logowanie z wykorzystaniem REST API
- **System ról**: Rozróżnienie między zwykłymi użytkownikami a administratorami
- **Personalizacja**: Indywidualne zarządzanie własnymi rezerwacjami

### Rezerwacje
- **Intuicyjne dodawanie**: Wybór daty, czasu i sali w prostym interfejsie
- **Inteligentne filtrowanie**: Wyświetlanie tylko dostępnych sal w wybranym terminie
- **Pełne zarządzanie**: Przeglądanie, edytowanie i usuwanie własnych rezerwacji
- **Gestural UI**: Usuwanie rezerwacji poprzez przeciągnięcie (swipe gesture)
- **Historia**: Podgląd zarówno aktualnych jak i przeszłych rezerwacji

### Zarządzanie Salami (Administratorzy)
- **CRUD Operations**: Pełne zarządzanie salami (dodawanie, edytowanie, usuwanie)
- **Kontrola dostępu**: Funkcje dostępne wyłącznie dla użytkowników z uprawnieniami administratora

## Stack Technologiczny

### Frontend (Mobile)
- **Język**: Kotlin
- **UI Framework**: Jetpack Compose z Material3 Design System
- **Architektura**: MVVM (Model-View-ViewModel)
- **State Management**: StateFlow + ViewModel
- **Navigation**: Jetpack Navigation Compose

### Networking & Data
- **HTTP Client**: Retrofit2 dla komunikacji z REST API
- **Local Storage**: SharedPreferences (dane autoryzacji)
- **Data Transfer**: JSON z wykorzystaniem DTO (Data Transfer Objects)

### Wymagania Systemowe
- **Android SDK**: Minimum API 26 (Android 8.0)
- **Development**: Android Studio Bumblebee lub nowszy
- **Target**: Współczesne urządzenia Android

## Instalacja i Uruchomienie

### Wymagania Wstępne
```bash
# Upewnij się, że masz zainstalowane:
- Android Studio (najnowsza wersja)
- Android SDK (min. API 26)
- Emulator Android lub fizyczne urządzenie
- Git
```

### Kroki Instalacji
```bash
# 1. Sklonuj repozytorium
git clone https://github.com/m4jorskyy/System_Rezerwacji_Sal.git

# 2. Przejdź do katalogu projektu
cd System_Rezerwacji_Sal

# 3. Otwórz projekt w Android Studio
# File -> Open -> wybierz folder projektu

# 4. Poczekaj na synchronizację Gradle

# 5. Skonfiguruj emulator lub podłącz urządzenie

# 6. Uruchom aplikację (Shift + F10 lub przycisk Run)
```

## Architektura Projektu

Projekt jest zorganizowany zgodnie z wzorcem MVVM i zasadami Clean Architecture:

```
src/main/java/com/example/systemrezerwacjisal/
├── data/
│   ├── api/           # Definicje Retrofit i interfejsy API
│   ├── model/         # Modele domenowe i DTO
│   └── local/         # Obsługa lokalnych danych (SharedPreferences)
└── ui/
    ├── navigation/    # Nawigacja między ekranami
    ├── screen/        # Ekrany aplikacji (Composable functions)
    ├── theme/         # Material Design theming
    └── viewmodel/     # ViewModels i logika biznesowa
```

## API Endpoints

Aplikacja komunikuje się z backendem poprzez następujące endpointy REST API:

### Autoryzacja
- `POST /api/users/login` - Logowanie użytkownika
- `POST /api/users/register` - Rejestracja nowego użytkownika

### Rezerwacje
- `GET /api/reservations/user/{id}` - Pobieranie rezerwacji użytkownika
- `POST /api/reservations` - Dodawanie nowej rezerwacji
- `PUT /api/reservations/{id}` - Edycja istniejącej rezerwacji
- `DELETE /api/reservations/{id}` - Usuwanie rezerwacji

### Sale
- `GET /api/rooms` - Pobieranie listy wszystkich sal
- `GET /api/rooms/filter` - Filtrowanie dostępnych sal
- `POST /api/rooms/admin` - Dodawanie nowej sali (admin)
- `PUT /api/rooms/admin/{id}` - Edycja sali (admin)
- `DELETE /api/rooms/admin/{id}` - Usuwanie sali (admin)

## Roadmap

Planowane funkcjonalności do implementacji w przyszłych wersjach:

- **Notyfikacje push**: Przypomnienia o nadchodzących rezerwacjach
- **Tryb offline**: Podgląd rezerwacji bez połączenia internetowego
- **Integracja z kalendarzem**: Synchronizacja z Google Calendar/Outlook
- **Chatbot AI**: Inteligentny asystent do zarządzania rezerwacjami
- **Analityka**: Dashboard z statystykami wykorzystania sal
- **Lokalizacja**: Wsparcie dla wielu języków

## Licencja

Projekt jest udostępniany na licencji MIT. Szczegóły w pliku [LICENSE](LICENSE).

## Autor

**Igor Suchodolski**
- Email: [igor.suchodolskii@gmail.com](mailto:igor.suchodolskii@gmail.com)
- GitHub: [@m4jorskyy](https://github.com/m4jorskyy)
