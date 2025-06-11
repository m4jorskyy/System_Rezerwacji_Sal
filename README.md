# Aplikacja Rezerwacji Sal

Aplikacja mobilna na platformę Android, umożliwiająca użytkownikom **rezerwację sal w budynkach uczelni lub firmy**. 

Użytkownicy mogą w łatwy sposób przeglądać dostępne terminy, zarządzać swoimi rezerwacjami, a także - jeśli posiadają uprawnienia administratora - dodawać oraz edytować sale.

## Funkcjonalności

Aplikacja oferuje kompleksowy zestaw funkcji, zapewniający intuicyjne zarządzanie rezerwacjami:

* **Rejestracja i logowanie użytkowników:** Bezpieczny dostęp do aplikacji.
* **Przegląd rezerwacji:** Wyświetlanie bieżących i przeszłych rezerwacji.
* **Dodawanie rezerwacji:**
    * Wybór daty i czasu.
    * Filtrowanie dostępnych sal.
    * Wybór konkretnej sali.
    * Definiowanie tytułu rezerwacji.
* **Edytowanie i usuwanie własnych rezerwacji:** Łatwa modyfikacja lub anulowanie rezerwacji (np. poprzez przeciągnięcie w bok).
* **Zarządzanie salami (tylko dla administratorów):** Dodawanie, edytowanie i usuwanie sal.
* **Obsługa ról:** Rozróżnienie między użytkownikiem a administratorem, zapewniające odpowiednie uprawnienia.
* **Prosty i responsywny interfejs:** Oparty na nowoczesnym zestawie narzędzi Jetpack Compose.

## Technologie

Projekt został zbudowany z wykorzystaniem najnowszych technologii Androida, zapewniających wydajność i skalowalność:

* **Język:** Kotlin
* **Interfejs użytkownika (UI):** Jetpack Compose (Material3)
* **Architektura:** MVVM (Model-View-ViewModel) z wykorzystaniem `ViewModel` i `StateFlow`
* **Networking:** Retrofit do obsługi REST API
* **Zarządzanie stanem:** `ViewModel` + `StateFlow`
* **Lokalne dane:** `SharedPreferences` (do przechowywania preferencji autoryzacji)

## Wymagania

Aby uruchomić projekt, upewnij się, że spełniasz następujące wymagania:

* **Android Studio Bumblebee** lub nowszy.
* **Android SDK** min. 26.
* Emulator Androida lub fizyczne urządzenie z systemem Android.

## Uruchomienie

Aby sklonować i uruchomić projekt lokalnie, wykonaj następujące kroki:

1.  **Sklonuj repozytorium:**
    ```bash
    git clone https://github.com/m4jorskyy/SystemRezerwacjiSalKonferencyjnych.git
    ```
3.  Otwórz projekt w **Android Studio**.
4.  Poczekaj na zakończenie synchronizacji **Gradle**.
5.  Uruchom aplikację na wybranym emulatorze lub fizycznym urządzeniu.

## Struktura projektu

Projekt jest zorganizowany w logiczny sposób, co ułatwia nawigację i rozwój:

  `/data/api`        - Definicje Retrofit i modele DTO (Data Transfer Objects)
  
  `/data/model`      - Modele domenowe aplikacji
  
  `/data/local`      - Obsługa lokalnych danych, np. SharedPreferences

  `/ui/navigation`   - Nawigacja między ekranami

  `/ui/screen`    - Poszczególne ekrany aplikacji (Compose)
  
  `/ui/theme`      - Definicje kolorów, typografii i kształtów Material Design
  
  `/ui/viewmodel`   - Implementacje ViewModel, logika biznesowa i zarządzanie stanami

## Używane endpointy API

Aplikacja komunikuje się z backendem za pośrednictwem następujących endpointów REST API:

* `POST /api/users/login` - Logowanie użytkownika
* `GET /api/reservations/user/{id}` - Pobieranie rezerwacji dla danego użytkownika
* `POST /api/reservations` - Dodawanie nowej rezerwacji
* `GET /api/rooms/filter` - Filtrowanie dostępnych sal
* `POST /api/rooms/admin` - Dodawanie nowej sali (tylko dla administratorów)
* `PUT /api/reservations/{id}` - Edycja istniejącej rezerwacji
* `DELETE /api/reservations/{id}` - Usuwanie rezerwacji

## Możliwe usprawnienia (TODO)

Poniżej lista funkcji, które można rozważyć w przyszłości, aby jeszcze bardziej rozwinąć aplikację:

* **Notyfikacje** o nadchodzących rezerwacjach.
* **Obsługa offline** (np. podgląd rezerwacji bez dostępu do internetu).
* **Integracja z kalendarzem** (Outlook/Google)
* Implementacja **chatbota AI**, w celu usprawnienia działania.

## Licencja

Projekt jest objęty licencją **MIT**. Więcej informacji znajdziesz w pliku `LICENSE`.

## Autor

**Igor Suchodolski**

* Email: igor.suchodolskii@gmail.com
