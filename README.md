# Lab 2 Starter: Availability Calculator

A small reservation component. Given a room's bookings and the day's business hours,
`AvailabilityCalculator.freeSlots` computes when the room is free. It is the code you
work in for Lab 2.

It ships with a generated test suite that passes, and a property-based test harness
(jqwik) with one example property. Everything is green. Your job in Lab 2 is to decide
whether green actually means correct.

**Read `ARCHITECTURE.md` before the code.**

## Build and test

```
mvn test
```

`mvn test` runs both files, the ordinary example-based tests (`AvailabilityCalculatorTest`)
and the property-based tests (`AvailabilityProperties`). A code-coverage report is written
to `target/site/jacoco/index.html`.

## Continuous integration

This repository has CI configured in `.github/workflows/ci.yml`. GitHub disables workflows on a
fresh fork, so enable them once on your fork (the handout shows where). After that, every
push runs `mvn test`. You will watch the gate go red when your new property finds the bug, then
green once you fix it.

## Where things are

- Component: `src/main/java/edu/cmu/cs214/availability/`
- Example-based tests: `src/test/java/edu/cmu/cs214/availability/AvailabilityCalculatorTest.java`
- Property-based tests: `src/test/java/edu/cmu/cs214/availability/AvailabilityProperties.java`
- Setup: `SETUP.md`

See the Lab 2 handout on the course page for the three milestones you show a TA.

## Milestone 3: auditing the generated suite

Three weaknesses in `AvailabilityCalculatorTest`:

1. **Every multi-booking test ends its last booking at 17:00** (*controllability*) — in five of
   the six tests the latest booking ends at `DAY_END`, so there is no trailing gap to drop and
   the buggy and fixed implementations return identical output.
2. **`returnedSlotsNeverOverlapABooking` reaches the bug but cannot see it** (*observability*) —
   it books only 10:00–11:00, so the buggy code silently loses 11:00–17:00, but the test asserts
   only that the slots it got back do not overlap. Nothing checks what is missing.
3. **No test passes an empty booking list** (*controllability*) — the input on which the bug is
   worst, since the buggy code reports a completely free day as having no availability at all.

High coverage did not save it because coverage measures which lines *ran*, not whether the
assertions *checked* the result — and the bug was an omission.

## Tools used

Claude Code, with Claude Opus 5.
