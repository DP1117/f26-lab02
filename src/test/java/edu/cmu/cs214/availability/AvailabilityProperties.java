package edu.cmu.cs214.availability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for {@link AvailabilityCalculator}.
 *
 * <p>One example property is provided below: it checks that no returned free slot
 * overlaps a booking, and it passes. In Milestone 1 you add a stronger property
 * that pins down what "correct availability" actually means. See the lab handout.
 */
class AvailabilityProperties {

    private final AvailabilityCalculator calc = new AvailabilityCalculator();

    /** Provided example: every returned free slot is genuinely free (overlaps no booking). */
    @Property
    void freeSlotsNeverOverlapABooking(@ForAll("scenarios") Scenario s) {
        List<TimeInterval> free = calc.freeSlots(s.dayStart(), s.dayEnd(), s.bookings());
        for (TimeInterval slot : free) {
            for (TimeInterval booking : s.bookings()) {
                assertFalse(slot.overlaps(booking),
                    () -> "free slot " + slot + " overlaps booking " + booking);
            }
        }
    }

    // --- Milestone 1: add your stronger property here ---

    /**
     * Milestone 1: the full specification of "correct availability".
     *
     * <p>Every minute of the business day {@code [dayStart, dayEnd)} is either covered
     * by a booking or reported as free. Never both, and never neither. The provided
     * property above only rules out "both": it inspects the slots that came back and
     * checks they are genuinely free. It says nothing about free time the calculator
     * failed to return, so an implementation that returns nothing at all would satisfy
     * it vacuously. The "never neither" half is what this property adds.
     *
     * <p>Checked one minute at a time, which is fine here: a day is at most 1440 minutes.
     */
    @Property
    void everyMinuteIsEitherBookedOrReportedFree(@ForAll("scenarios") Scenario s) {
        List<TimeInterval> free = calc.freeSlots(s.dayStart(), s.dayEnd(), s.bookings());

        for (int minute = s.dayStart(); minute < s.dayEnd(); minute++) {
            TimeInterval thisMinute = new TimeInterval(minute, minute + 1);
            boolean booked = s.bookings().stream().anyMatch(thisMinute::overlaps);
            boolean reportedFree = free.stream().anyMatch(thisMinute::overlaps);

            int m = minute;
            assertNotEquals(booked, reportedFree,
                () -> "minute " + m + " is "
                    + (booked ? "BOTH booked and reported free" : "NEITHER booked nor reported free")
                    + "; day = [" + s.dayStart() + ", " + s.dayEnd() + ")"
                    + ", bookings = " + s.bookings()
                    + ", free = " + free);
        }
    }

    /** Generates a business day plus a list of bookings (possibly unsorted, overlapping, or outside hours). */
    @Provide
    Arbitrary<Scenario> scenarios() {
        Arbitrary<Integer> minutes = Arbitraries.integers().between(0, 1440);
        Arbitrary<TimeInterval> intervals = Combinators.combine(minutes, minutes)
            .as((a, b) -> new TimeInterval(Math.min(a, b), Math.max(a, b) + 1));
        Arbitrary<List<TimeInterval>> bookings = intervals.list().ofMaxSize(6);
        return Combinators.combine(minutes, minutes, bookings)
            .as((a, b, bk) -> new Scenario(Math.min(a, b), Math.max(a, b) + 1, bk));
    }

    record Scenario(int dayStart, int dayEnd, List<TimeInterval> bookings) {
    }
}
