# Stage 1 reproducible test notes

These notes cover the base setting at `stage-1`, commit `a43030803ae977ded3410d6b2b51048b72f74117`. This historical snapshot has the 15 Stage 1 Java source files and no Stage 2 or Stage 3 Java features. The notes and replay files were added later on `main`; they do not change the historical code. Its Stage 1 source is identical to the corresponding source on `main` when these notes were added.

## Build and replay

Run these commands from the repository root with Java 17 or later. Each run starts a fresh game. Compile the source into a separate folder; the committed `.class` files are not needed. Use the default seed-42 mode, without `--cycle`.

```sh
javac --release 17 -d /tmp/restaurant-rush-stage1 "Stage 1 Files/JavaFiles/"*.java
java -cp /tmp/restaurant-rush-stage1 RestaurantGameApp < "Stage 1 Files/Tests/victory-input.txt"
java -cp /tmp/restaurant-rush-stage1 RestaurantGameApp < "Stage 1 Files/Tests/defeat-input.txt"
```

The input files contain one menu response per line, including customer, table and order IDs. They are also usable for manual replay using the turn table below. Run them in a terminal that supports ANSI colours, or ignore the colour escape codes in redirected output.

## Victory run

Follow these Waiter and Chef assignments. All arrivals are Regular customers. The manager chooses priorities, not menu items.

| Turn | Waiter | Chef |
|---|---|---|
| 1 | Seat #1 at Table 1 | Wait |
| 2 | Take order at Table 1 | Cook Order #1 |
| 3 | Wait | Cook Order #1 |
| 4 | Serve Order #1 | Wait |
| 5 | Seat #2 at Table 2 | Wait |
| 6 | Take order at Table 2 | Cook Order #2 |
| 7 | Serve Order #2 | Wait |
| 8 | Seat #3 at Table 1 | Wait |
| 9 | Take order at Table 1 | Cook Order #3 |
| 10 | Serve Order #3 | Wait |
| 11 | Wait | Wait |
| 12 | Seat #4 at Table 2 | Wait |
| 13 | Seat #5 at Table 1 | Wait |
| 14 | Take order at Table 1 | Cook Order #4 |
| 15 | Take order at Table 2 | Cook Order #4 |
| 16 | Serve Order #4 | Cook Order #5 |
| 17 | Serve Order #5 | Wait |
| 18 | Wait | Wait |

Expected result after Turn 18:

| Measure | Expected |
|---|---|
| Result | Victory |
| Turns used | 18 |
| Dishes served | 5 |
| Paid customers | 4 (#1, #2, #3 and #5) |
| Revenue | $54.00 |
| Average paid satisfaction | 76.00 |
| Unhappy departures | 0 |

Paid bills are $18.00, $9.00, $9.00 and $18.00. Recorded satisfaction is 76, 76, 76 and 76. Customer #4 is served on Turn 17 but cannot pay before the game ends. This explains why the served and paid counts differ.

## Defeat run

For all 18 turns, choose Waiter option `4` (Wait), then Chef option `2` (Wait). The defeat input repeats these two responses 18 times.

Expected result: **Defeat**, 18 turns used, 0 dishes served, 0 paid customers, $0.00 revenue, average paid satisfaction `N/A`, and 2 unhappy departures. Customers still waiting when Turn 18 ends are not automatically counted as departures.

## Verification

Verified on 13 November 2026 from the historical commit's Java source, using JDK 26 with `javac --release 17`. Both supplied input files were replayed through `RestaurantGameApp` and completed successfully with the results above. No game source or random choices were modified for verification.
