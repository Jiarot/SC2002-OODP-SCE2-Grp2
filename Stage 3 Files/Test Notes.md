# Stage 3 reproducible test notes

These notes cover the base setting at `stage-3`, commit `7aefd8e8fa7e8c296f1ceeddb6d6ab3ec88abc23`. This snapshot contains all 23 Stage 3 Java source files, with the Host, combos and Happy Hour. The notes and replay files were added later on `main`; they do not change the historical code. Its Stage 3 source is identical to the corresponding source on `main` when these notes were added.

## Build and replay

Run these commands from the repository root with Java 17 or later. Each run starts a fresh game. Compile the source into a separate folder; the committed `.class` files are not needed. Use the default seed-42 mode, without `--cycle`.

```sh
javac --release 17 -d /tmp/restaurant-rush-stage3 "Stage 3 Files/Java Files/"*.java
java -cp /tmp/restaurant-rush-stage3 RestaurantGameApp < "Stage 3 Files/Tests/victory-input.txt"
java -cp /tmp/restaurant-rush-stage3 RestaurantGameApp < "Stage 3 Files/Tests/defeat-input.txt"
```

The input files contain one menu response per line, including customer, table and order IDs. They are also usable for manual replay using the turn table below. Run them in a terminal that supports ANSI colours, or ignore the colour escape codes in redirected output.

## Victory run

Keep Happy Hour for later on Turns 1–6, then activate it on Turn 7. The Host seats a waiting customer whenever a table is free. The Waiter prioritises a READY order, then taking an order. The Chef advances the oldest unfinished order.

| Turn | Happy Hour | Host | Waiter | Chef |
|---|---|---|---|---|
| 1 | Keep for later | Seat #1 at Table 1 | Take order at Table 1 | Cook Order #1 |
| 2 | Keep for later | Wait (automatic) | Wait | Cook Order #1 |
| 3 | Keep for later | Wait (automatic) | Wait | Cook Order #1 |
| 4 | Keep for later | Seat #2 at Table 2 | Serve Order #1 | Wait |
| 5 | Keep for later | Wait (automatic) | Take order at Table 2 | Cook Order #2 |
| 6 | Keep for later | Wait (automatic) | Serve Order #2 | Wait |
| 7 | Activate | Seat #3 at Table 1 | Take order at Table 1 | Cook Order #3 |
| 8 | No prompt (active) | Wait (automatic) | Wait | Cook Order #3 |
| 9 | No prompt (finished) | Wait (automatic) | Serve Order #3 | Wait |
| 10 | No prompt (finished) | Seat #4 at Table 2 | Take order at Table 2 | Cook Order #4 |
| 11 | No prompt (finished) | Wait (automatic) | Wait | Cook Order #4 |
| 12 | No prompt (finished) | Wait (automatic) | Wait | Cook Order #4 |
| 13 | No prompt (finished) | Seat #5 at Table 1 | Serve Order #4 | Wait |
| 14 | No prompt (finished) | Wait (automatic) | Take order at Table 1 | Cook Order #5 |
| 15 | No prompt (finished) | Wait (automatic) | Wait | Cook Order #5 |
| 16 | No prompt (finished) | Seat #6 at Table 2 | Serve Order #5 | Wait |
| 17 | No prompt (finished) | Wait (automatic) | Take order at Table 2 | Cook Order #6 |
| 18 | No prompt (finished) | Wait (automatic) | Serve Order #6 | Wait |

The Host prompt is skipped automatically when nobody can be seated. Happy Hour is active on Turns 7 and 8; after activation, no further activation prompt appears. The supplied input file accounts for these skipped prompts.

Expected result after Turn 18:

| Measure | Expected |
|---|---|
| Result | Victory for every Critic reaction |
| Turns used | 18 |
| Dishes served | 6 |
| Paid customers | 5 (#1–#5) |
| Revenue | $92.59 |
| Unhappy departures | 0 |
| Average paid satisfaction | 68.00, 80.80 or 88.00, as explained below |

### Orders and payment evidence

| Customer | Ordered | Dish | Bill locked at ordering | Served | Paid | Recorded satisfaction |
|---|---|---|---|---|---|---|
| #1 Critic | T1 | Pasta + Salad Combo | $24.30 | T4 | T6 | 0, 64 or 100 |
| #2 Regular | T5 | Salad | $9.00 | T6 | T8 | 84 |
| #3 VIP | T7 | Burger + Salad Combo | $15.55 | T9 | T11 | 95 |
| #4 Regular | T10 | Pasta + Salad Combo | $24.30 | T13 | T15 | 76 |
| #5 VIP | T14 | Burger + Salad Combo | $19.44 | T16 | T18 | 85 |
| #6 Regular | T17 | Burger | $15.00 (unpaid) | T18 | After game ends | Not included |

Revenue includes only Customers #1–#5: $24.30 + $9.00 + $15.55 + $24.30 + $19.44 = **$92.59**.

Customer #3 demonstrates both VIP and Happy Hour pricing: 2160 cents × 90% = 1944 cents, then 1944 × 80% = 1555.2 cents, rounded to **1555 cents**. That bill remains $15.55 when the customer pays on Turn 11, after Happy Hour has ended. The Turn 8 recovery restores this customer's waiting satisfaction to 100 before the turn-end penalty.

### Reproducibility and the Critic

Seed 42 fixes the dish sequence. The Critic has an independent, unseeded random reaction, so the exact satisfaction total is not fixed. This replay guarantees the same victory outcome, payments and revenue for every possible reaction:

| Reaction | Critic satisfaction | Average of five paid customers |
|---|---|---|
| FURIOUS | 0 | (0 + 84 + 95 + 76 + 85) / 5 = 68.00 |
| UNIMPRESSED | 64 | (64 + 84 + 95 + 76 + 85) / 5 = 80.80 |
| DELIGHTED | 100 | (100 + 84 + 95 + 76 + 85) / 5 = 88.00 |

Even the worst case exceeds the required average of 60. A furious Critic still pays because they have already been served; they are not an unhappy departure under the waiting-abandonment rule.

## Defeat run

On every turn, keep Happy Hour for later (`2`), choose Host Wait (`2`), Waiter Wait (`4`), and Chef Wait (`2`). The defeat input repeats `2, 2, 4, 2` across 18 turns. Since nobody is seated, a free table and waiting customer remain available and the Host prompt appears each turn.

Expected result: **Defeat**, 18 turns used, 0 dishes served, 0 paid customers, $0.00 revenue, average paid satisfaction `N/A`, and 2 unhappy departures. The arriving Critic and first Regular customer abandon; the remaining customers have not reached zero when the game ends.

## Verification

Verified on 13 November 2026 from the historical snapshot's Java source, using JDK 26 with `javac --release 17`. The victory input was replayed **60 times through the actual command-line application**, rather than only calling controller methods. All runs won with $92.59 revenue, 5 paid customers and 0 unhappy departures. All three satisfaction outcomes occurred: FURIOUS 18 times, UNIMPRESSED 17 times and DELIGHTED 25 times. These sample counts are observations, not fixed expected frequencies.

The supplied defeat input also completed successfully with the expected result. No game source or random choices were modified for verification.
