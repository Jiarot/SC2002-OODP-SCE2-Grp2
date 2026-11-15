# SC2002-OODP-SCE2-Grp2
SC2002 Restaurant Rush Group Assignment

## Verified stage snapshots and reproducible runs

| Stage | Historical commit | Source folder | Test notes |
|---|---|---|---|
| [stage-1](https://github.com/Jiarot/SC2002-OODP-SCE2-Grp2/tree/stage-1) | `a43030803ae9` | `Stage 1 Files/JavaFiles` | [Stage 1 victory and defeat](Stage%201%20Files/Test%20Notes.md) |
| [stage-2](https://github.com/Jiarot/SC2002-OODP-SCE2-Grp2/tree/stage-2) | `fd439e4764bb` | `Stage 2 Files/JavaFiles` | [Stage 2 README and test notes](Stage%202%20Files/Stage%202%20README.md) |
| [stage-3](https://github.com/Jiarot/SC2002-OODP-SCE2-Grp2/tree/stage-3) | `7aefd8e8fa7e` | `Stage 3 Files/Java Files` | [Stage 3 victory and defeat](Stage%203%20Files/Test%20Notes.md) |

The tags identify existing historical commits and were added retrospectively. Stage 1 contains no Stage 2/3 Java features; Stage 2 contains no Stage 3 Java features. All three source versions compile independently with Java 17 compatibility. The Stage 1 and Stage 3 test notes and replay files were added later on `main`; they are not present inside the historical tags. Download or retain the replay files from `main` when testing a tagged checkout.

Stage comparisons: [Stage 1 → 2](https://github.com/Jiarot/SC2002-OODP-SCE2-Grp2/compare/stage-1...stage-2) · [Stage 2 → 3](https://github.com/Jiarot/SC2002-OODP-SCE2-Grp2/compare/stage-2...stage-3).

Build/run commands and exact replay inputs are in the linked test notes. Compile only one stage at a time because the stages reuse class names.

## Presentation video and timestamps

**Video:** `Group 2 Video Demonstration SC2002 - Captioned.mp4` (included with the group submission)  
**Duration:** 14 minutes 41 seconds  
**Group members:** Ernest, Triton, Jackson, Calvin and Sze Waye  
**Repository:** [SC2002-OODP-SCE2-Grp2](https://github.com/TritonXander/SC2002-OODP-SCE2-Grp2)

All timestamps use `MM:SS`, measured from the start of the video. They mark the start of the explanation; the corresponding screen may change shortly afterwards. The original and captioned videos use the same timeline.

## Main sections

| Time range | Section | Content |
|---|---|---|
| 00:00 - 02:49 | Game overview and the three stages | Base game, turn sequence, customer types, Stage 3 features and the overall design approach. |
| 02:49 - 08:23 | Gameplay demonstration and outcomes | Staff task limits, combo preparation, serving, Critic behaviour, payment, Happy Hour, victory and defeat. |
| 08:23 - 10:48 | Class diagrams and stage changes | How the class design develops from Stage 1 to Stage 3 through inheritance, polymorphism, interfaces and composition. |
| 10:48 - 12:53 | Stage 3 sequence diagram | The Critic's order lifecycle, including arrival, seating, ordering, preparation, serving and payment. |
| 12:53 - 14:41 | Individual contributions and learning | Each member explains their contribution and key learning. Spoken content ends at approximately 14:33. |

## Detailed timestamps

### Game overview

| Start | Topic |
|---|---|
| 00:00 | Introduction to Group 2's Restaurant Rush project. |
| 00:16 | Stage 1: baseline game, staff roles, customer arrivals and victory conditions. |
| 00:43 | The five steps in each turn: arrivals, Waiter action, Chef action, payment and waiting penalties. |
| 01:04 | Stage 2: VIP and Critic behaviour, customer subclasses and polymorphism. |
| 01:55 | Stage 3: Host, combo meals, Happy Hour and the supporting abstractions. |

### Gameplay demonstration

| Start | Presenter | Topic |
|---|---|---|
| 02:49 | Triton | Launching Stage 3, the manager's role, staff task limits and seating the Critic. |
| 04:10 | Jackson | Following a pasta-and-salad combo through preparation, serving, the Critic's reaction and payment. |
| 06:18 | Calvin | Stage 3 features, combo eligibility, Happy Hour satisfaction recovery and discounts on new orders. |
| 07:21 | Calvin | A separate successful 18-turn run: four paid customers, $54.54 revenue and average satisfaction above 60. |
| 07:57 | Triton | A separate defeat run: staff wait, customers abandon the restaurant, and no payments are collected. |

### Class diagrams and design evolution

| Start | Topic |
|---|---|
| 08:23 | Stage 1: core classes, controller coordination and separation of responsibilities. |
| 08:53 | Stage 2: extending `Customer` with VIP and Critic subclasses; overriding billing and food-reaction methods. |
| 09:30 | Stage 3: `StaffMember`, the `Dish` interface, `ComboMeal` and `HappyHour`. |
| 10:23 | Comparing the stages: code reuse, extensibility and changes that preserve the order lifecycle. |

### Sequence diagram: Stage 3 Critic order lifecycle

| Start | Topic |
|---|---|
| 10:48 | Use case and starting conditions: Critic #1, reproducible menu choices using `--cycle`, and the optional Happy Hour event. |
| 11:07 | `ConsoleUI` and `RestaurantController` interactions, staff reset and customer arrival. |
| 11:27 | Host seating and Waiter ordering within the same turn. |
| 11:37 | Food selection, combo eligibility and bill locking. |
| 11:58 | Chef preparation and why an order cannot be served in the turn it becomes ready. |
| 12:09 | Serving and dynamic dispatch to `CriticCustomer.reactToServedFood()`. |
| 12:31 | Eating, readiness to pay and the Cashier's payment timing rule. |
| 12:43 | Payment completes the use case: revenue and satisfaction are recorded, and the table is freed. |

## Individual contributions and key learning

| Time range | Member | Contribution and learning discussed |
|---|---|---|
| 12:53 - 13:13 | Ernest | Stage design, comparing implementation approaches, checking that each stage runs independently and documenting design evolution. |
| 13:13 - 13:30 | Triton | Gameplay mechanics, staff constraints, defeat scenarios, resource management and systematic testing. |
| 13:30 - 13:52 | Jackson | Order-processing lifecycle, customer behaviour, victory conditions and polymorphism. |
| 13:52 - 14:11 | Calvin | Stage 3 features, class-design evolution, abstraction, inheritance and interfaces. |
| 14:11 - 14:33 | Sze Waye | Stage 1 and Stage 2 implementation, class and sequence diagrams, and how early design decisions affect later extensions. |
