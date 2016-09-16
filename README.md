# android-common

## Overview

### Compkit

#### SortedList

#### BaseRecyclerAdapter

### Foundation

#### MVP

Contains abstract classes for MVP concept:
- `BasePresenterImpl` (and interface)
- `BaseActivityImpl` (and interface)
- `BaseFragmentImpl` (and interface)
- `BaseListFragmentImpl` (and interface)
- `BaseServiceImpl` (and interface)

There is no base class for interactor.

Repo interface for handling object in list fashion. Exposes common CRUD functionality. It has `MemoryRepoImpl` which is able to store object in memory. Models which are supposed to be stored in Repo have to implement BaseModel interface. `android-firebase` github reposiotry also implements `FirebaseRepoImpl`.

Activity clasess with one to one realtion (activity to fragment) for basic use cases:
- `DialogFragmentActivityImpl` - for displaying dialog like fragments
- `SimpleFragmentActivityImpl` - for displaying fragment in full view

#### Containers

Containers classes for wrapping multiple object into one. Helpful in Rx style programming.

#### Rx classes

Custom schedulers, RetryBuilder, BackPressureSubscriber, EmptySubscriber