# Magic Marbles

- [Magic Marbles](#magic-marbles)
  - [Running The App](#running-the-app)
    - [Local](#local)
      - [Prerequisites](#prerequisites)
      - [Starting The App](#starting-the-app)
    - [Online](#online)
  - [Implementation](#implementation)
    - [Common Patterns](#common-patterns)
      - [Abstract Factory](#abstract-factory)
      - [Result Monad](#result-monad)
      - [Dependency Injection](#dependency-injection)
    - [Used Libraries](#used-libraries)
    - [Architecture](#architecture)
      - [Core](#core)
      - [Core-Impl](#core-impl)
      - [UI](#ui)
        - [API](#api)
        - [Frontend](#frontend)
          - [Screenshots](#screenshots)
    - [SOLID - Principles](#solid---principles)
      - [S - Single Responsibility Principle](#s---single-responsibility-principle)
      - [O - Open-Closed Principle](#o---open-closed-principle)
      - [L - Liskov Substitution Principle](#l---liskov-substitution-principle)
      - [I - Interface Segregation Principle](#i---interface-segregation-principle)
      - [D - Dependency Inversion Principle](#d---dependency-inversion-principle)

## Running The App

### Local

#### Prerequisites

- JDK (at least 8. It was tested with JDK 8 and 11 but others should work fine) installed & `JAVA_HOME` set

#### Starting The App

1. Navigate the root directory and run `.\gradlew.bat :ui:run` to start the application
2. Open the browser and navigate to `localhost:8080` to play the game
3. to change the port start the gradle task with `--args="-port=9090"`

### Online

Launch it via [https://magic-marbles.azurewebsites.net/](https://magic-marbles.azurewebsites.net/) (Though it's not guaranteed to work since its running on a free azure instance)

## Implementation

The Magic-Marbles application consists of three modules.
The `core` module containing of all the interfaces needed in the application.
The `core-impl` which implements those interfaces. And the `ui` module provides a web-app to play the game.

The details of the architecture can be found in [Architecture](#architecture).

### Common Patterns

#### Abstract Factory

A commonly used pattern in the implementation is the _Abstract Factory_.
The creation of e.g. a `Game` instance or the field of a game is all done via factories. This allows it to create a different implementation for the game/field without changing the implementation.

#### Result Monad

Another commonly used "pattern" is the use of `Result` Monads as return type. This means an operation can either yield a successful result value or an error.
As an example the method to remove a marble from a field returns either the amount of marbles which where removed or an exception which describes the occurred error.

With methods like `map` or `flatMap` the successful result value of a `Result` can be used to do further operations and with methods like `onSuccess` specific code can be executed when the `Result` contains a successful value.
The advantage of this is reduced boilerplate code for error handling, which also results in a more readable code.

#### Dependency Injection

Additionally all dependencies are injected via dependency injection. This allows for loose coupling between components and a better extensibility.

### Used Libraries

- Frontend
  - Vue: SPA Framework (<https://github.com/vuejs/vue>)
  - Vuex: State Management Library (<https://github.com/vuejs/vuex>)
- Backend
  - Result: Result Monad for Kotlin (<https://github.com/michaelbull/kotlin-result>)
  - KTOR: Web Server for Kotlin (<https://github.com/ktorio/ktor>)
  - kotlinx.serialization: JSON Serializer for Kotlin (<https://github.com/Kotlin/kotlinx.serialization>)
  - Kodein: Kotlin Dependency Injection Framework (<https://github.com/Kodein-Framework/Kodein-DI>)

### Architecture

#### Core

Diagram of all provided interfaces

![core](./doc/images/api.svg)

The `core` module provides the interfaces as well as some data containers like `Color` and `Marble` for the magic marble game.
The two primary interfaces are `Game` which provides the operations to play the game and `Field` which represents the field of the game. The `Field` interface is abstracted further and has two sub interfaces `PlayableField` and `ModifiableField`. The `ModifiableField` is contains methods for modifying the field and is used for its initialization and the `PlayableField` which contains methods for playing the game.
The idea behind this is, that the creation and initialization of a field is encapsulated in the `FieldBuilder`, which prevents the user of a field to modify the field himself. Through the abstraction of the `ModifiableFieldFactory` the concrete implementation used in the `FieldBuilder` can be swapped out easily.

The `GameFactory` as well as the `FieldBuilder` can be parameterized via a `Settings` argument. To allow for better extension both `GameFactory` and `FieldBuilder` have a generic parameter which must extends from `Settings`. This makes it possible to pass additional `Settings`. This is used in the [Core-Impl](#core-impl) to pass `ExtendedSettings` to the factory.

#### Core-Impl

Diagram of all interfaces with respective implementation
![impl](./doc/images/full.svg)

The `core-impl` module uses the interfaces defined in [Core](#core) and implements them.

 Furthermore it defines a `SettingsValidator` for validating Settings and providing error messages, as well as `Configuration` which is a container for various configuration parameters. It is used for example by the `SettingsValidator` to check if the Settings are valid. This allows changing those parameters without changing the validation logic.

This module also provides `ExtendedSettings` and a factory `ExtendedSettingsFactory`.
`ExtendedSettings` allow for customization on how many points should be removed per marble at the end of the game, how many marbles need to be connected to be able to be removed and how points should be calculated.

An interesting implementation detail is, that the `GameImpl` does not directly use a `FieldBuilder` to create a field. Rather the `GameFactory` checks if the Settings are valid for creating a `Field` via the `validate` method of the `FieldBuilder` and then passes an anonymous function to `GameImpl` which internally calls the `build` method of the `FieldBuilder`, since its already known that the `Settings` for creating a `Field` are valid the `Result` can be unwrapped. This means `GameImpl` gets a function which takes no parameters and returns a newly created `Field`. This way the `GameImpl` doesn't have to bother with the potential failure of the `FieldBuilder`.
Furthermore it is easier to restart the game since the `GameImpl` only has to call the function again to create a new field.

#### UI

The `ui` module consists of two parts, the web API which handles requests and the frontend for displaying the game.

##### API

The API is the communication endpoint for the frontend. To associate a player with a game, the player gets a session token from the API in form of a cookie.
The API exposes four operation with which the frontend can "modify" the current game.
These operations are `sync`, `startWithConfiguration`, `move` and `hover`

`sync` is used to synchronize the game state at application start-up. For example if a player closes the browser window and opens it again the application will sync the last known state so the player can continue where he left off.

Furthermore the game uses a "syncing strategy" to prevent players from playing the game in an outdated state. For example the player plays the game and then opens a second browser window to continue the game there (Which is possible because of the aforementioned session token).
To prevent the player from playing on an outdated state in the original window, each game state gets a unique id.
If the player makes a move or starts a new game a new id is generated. This id is exchanged between the frontend and API, so that the API can check on every request that the player has the most recent game state.
If the frontend provides an id does not match the latest one stored by the API, the API provides the latest state. To distinct this to a normal response the HTTP Statuscode 409 Conflict is used.

With `startWithConfiguration` a new game with the provided settings can be started. These settings are validated, and any errors will be returned.

With `move` the frontend can remove connected marbles.

The `hover` method returns the coordinates of all connected marbles for a given marble. It is used by the frontend to visualize which marbles are connected.

##### Frontend

For the frontend the SPA-Framework Vue was used in combination with Vuex as state management library.
Through the UI the user can:

- Start a game, by entering the desired settings
- Restart a game with the current configuration
- Start a new game during the currently running one with different settings
- Remove connected marbles
- Hover over marbles to see which are connected

###### Screenshots

Start Screen

![Start Screen](./doc/images/start_screen.png)

Game Screen

![Game Screen](./doc/images/game_screen.png)

Hover Indication

![Hover](./doc/images/game_screen_hover.png)

### SOLID - Principles

The magic marbles game was implemented with the five solid principles in mind. In the following sections for every principle it is described how it was used in the implementation.

#### S - Single Responsibility Principle

The implementation strictly follows the single responsibility principle by separating concerns. An example of this is the split of the `Game` and `Field` where the `Game` is solely concerned with managing the game (checking if a game is over and how many points a player achieved) and the `Field` is only responsible for managing the field state.

#### O - Open-Closed Principle

The open-closed principle does also find broad application in the implementation of magic marbles.
Through the abstraction through interfaces and the injection of those interfaces, the system is open for extension since new interface implementations can be created and used without modification of existing code.
But the system is also closed for modification since new implementations are bound by a given API interface.

#### L - Liskov Substitution Principle

The implementation of the magic marbles game also follows the liskov substitution principle since every class/interface can be replaced by its subclass/implementation. This is possible through careful interface definition, to prevent defining an interface a implementation would not conform to.

#### I - Interface Segregation Principle

The implementation also follows the interface segregation principle to not provide the client with an interface it will never use. A prime example is the split between `Field`, `PlayableField` and `ModifiableField`. With this split the implementation of a new field is much more flexible in comparison to a single `Field` interface.

#### D - Dependency Inversion Principle

The magic marbles game heavily adapts the dependency inversion principle by providing every dependency in form of an injected variable. This greatly decreases coupling and increases the adaptability of the system.
