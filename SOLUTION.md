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
empty, so its loop body never runs and not one assertion executes. The underlying bug
is that `freeSlots` emits the gap *before* each booking but never the trailing gap from
the cursor to `dayEnd`, so free time after the last booking is dropped; a normal
9:00–17:00 day with a single 10:00–11:00 meeting returns only 9:00–10:00 and loses the
whole afternoon. The failing run reported:

```
minute 0 is NEITHER booked nor reported free; day = [0, 1), bookings = [], free = []
```

with the six example tests and the provided property all still passing in that same run.

**Milestones 2 and 3:** not started.
