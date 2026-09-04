# Lab 2 — what to tell the TA

**Milestone 1.** The provided property quantifies over the calculator's *output* — it
loops the returned slots and checks each is genuinely free — so it is structurally
blind to free time that was never returned, and an implementation returning `[]` for
every input would pass it perfectly. My property, `everyMinuteIsEitherBookedOrReportedFree`,
quantifies over the *input domain* instead: for each minute of `[dayStart, dayEnd)` it
asks "is this minute booked?" and "is it reported free?" and asserts the answers
disagree, which pins down both halves of the spec — never both, and never neither.
jqwik failed it on the first try with `Scenario[dayStart=0, dayEnd=1, bookings=[]]`: a
one-minute day, nothing booked, and the calculator returns `[]`, so minute 0 is neither
booked nor free. The provided property passes on that same input vacuously — `free` is
empty, so its loop body never runs and not one assertion executes.

```
                              |-----------------------jqwik-----------------------
tries = 1                     | # of calls to property
checks = 1                    | # of not rejected calls
generation = RANDOMIZED       | parameters are randomly generated
after-failure = SAMPLE_FIRST  | try previously failed sample, then previous seed
when-fixed-seed = ALLOW       | fixing the random seed is allowed
edge-cases#mode = MIXIN       | edge cases are mixed in
edge-cases#total = 650        | # of all combined edge cases
edge-cases#tried = 1          | # of edge cases tried in current run
seed = -6576973228387177418   | random seed to reproduce generated values

Sample
------
  arg0: Scenario[dayStart=0, dayEnd=1, bookings=[]]

....

minute 0 is NEITHER booked nor reported free; day = [0, 1), bookings = [], free = []
```

**Milestone 2.** The bug: `freeSlots` emitted the gap *before* each booking but never the
trailing gap from the last booking to `dayEnd`, so free time after the final meeting was
silently dropped — and with no bookings at all the cursor never moved and the whole day
vanished. The fix adds that trailing gap after the loop, guarded by `cursor < dayEnd`
because `TimeInterval` rejects empty intervals and a booking running to the end of the day
leaves no gap to emit. Three lines in `AvailabilityCalculator`; the property was not
touched. All 8 tests now pass, with the property clearing 1000 generated cases including
the replayed counterexample, and the 9:00–17:00 day with a 10:00–11:00 meeting now returns
both 9:00–10:00 and 11:00–17:00.

**Milestone 3:** not started.
