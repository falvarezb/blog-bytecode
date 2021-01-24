package fjab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A ZoneId is used to identify the rules used to convert between an Instant and a LocalDateTime
 *
 * Instant + ZoneId --> LocalDateTime
 */
public class JavaTimeTest {

  ZoneId parisZoneId = ZoneId.of("Europe/Paris");
  ZoneId londonZoneId = ZoneId.of("Europe/London");
  LocalDateTime parisDstGap = LocalDateTime.of(2021,3,28,2,20,0);
  LocalDateTime parisDstOverlap = LocalDateTime.of(2021,10,31,2,20,0);
  ZoneRules parisRules = parisZoneId.getRules();

  @Nested
  @DisplayName("convert Instant to LocalDateTime on UTC")
  class ConvertInstantToLocalDateTimeUTC {
    Instant instant = Instant.parse("2030-12-31T03:00:00Z");

    @DisplayName("using ZoneOffset ids")
    @Test
    public void usingZoneOffsetIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 31, 3, 0, 0));
    }

    @DisplayName("using offset-style ids")
    @Test
    public void usingOffsetStyleIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 31, 3, 0, 0));
    }
  }


  @Nested
  @DisplayName("convert Instant to LocalDateTime with positive offset")
  class ConvertInstantToLocalDateTimePositiveOffset {
    Instant instant = Instant.parse("2030-12-30T23:00:00Z");

    @DisplayName("using ZoneOffset ids")
    @Test
    public void usingZoneOffsetIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.of("+2"));
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 31, 1, 0, 0));
    }

    @DisplayName("using offset-style ids")
    @Test
    public void usingOffsetStyleIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC+2"));
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 31, 1, 0, 0));
    }

    @DisplayName("using region-based ids")
    @Test
    public void usingRegionBasedIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, parisZoneId);
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 31, 0, 0, 0));
    }
  }


  @Nested
  @DisplayName("convert Instant to LocalDateTime with negative offset")
  class ConvertInstantToLocalDateTimeNegativeOffset {
    Instant instant = Instant.parse("2030-12-31T01:00:00Z");

    @DisplayName("using ZoneOffset ids")
    @Test
    public void usingZoneOffsetIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.of("-2"));
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 30, 23, 0, 0));
    }

    @DisplayName("using offset-style ids")
    @Test
    public void usingOffsetStyleIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC-2"));
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 30, 23, 0, 0));
    }

    @DisplayName("using region-based ids")
    @Test
    public void usingRegionBasedIds() {
      var localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/Sao_Paulo"));
      //WRONG: it should be UTC-3 as Brazil no longer observes DST
      //see https://www.oracle.com/java/technologies/tzdata-versions.html
      assertEquals(localDateTime, LocalDateTime.of(2030, 12, 30, 23, 0, 0));
    }
  }


  @DisplayName("daylight saving time (DST)")
  @Nested
  class DaylightSavingTime {

    ZoneRules parisRules = parisZoneId.getRules();
    Instant parisWinter = Instant.parse("2021-01-31T00:00:00Z");
    Instant parisSummer = Instant.parse("2021-07-31T00:00:00Z");

    @DisplayName("fixed offset: there is no DST")
    @Test
    public void fixedOffset() {
      assertTrue(ZoneId.of("UTC+2").getRules().getDaylightSavings(parisWinter).isZero());

      Instant summer = Instant.parse("2021-07-31T00:00:00Z");
      assertTrue(ZoneId.of("UTC+2").getRules().getDaylightSavings(summer).isZero());
    }

    @DisplayName("geographical region: encodes zone rules like DST rules")
    @Test
    public void region() {
      assertTrue(parisRules.getDaylightSavings(parisWinter).isZero());

      Instant summer = Instant.parse("2021-07-31T00:00:00Z");
      assertEquals(parisRules.getDaylightSavings(summer).getSeconds(), 3600);
    }

    @DisplayName("DST gaps when moving from winter to summer time: clocks jump forward")
    @Test
    public void dstGaps() {
      assertEquals("Transition[Gap at 2021-03-28T02:00+01:00 to +02:00]", parisRules.nextTransition(parisWinter).toString());

      //There is no valid offset for the gap time as the clock jumps
      assertTrue(parisRules.getValidOffsets(parisDstGap).isEmpty());
      //By default, the offset used is the earlier one. This default behaviour may not be valid for some applications
      assertEquals("+01:00", parisRules.getOffset(parisDstGap).toString());
    }

    @DisplayName("DST overlaps when moving from summer to winter time: clocks are set back")
    @Test
    public void dstOverlaps() {
      assertEquals("Transition[Overlap at 2021-10-31T03:00+02:00 to +01:00]", parisRules.nextTransition(parisSummer).toString());

      //There are 2 offsets for the overlap time as the clock moves twice through that time
      assertArrayEquals(parisRules.getValidOffsets(parisDstOverlap).stream().map(ZoneOffset::toString).toArray(), new String[]{"+02:00","+01:00"});
      //By default, the offset used is the earlier one. This default behaviour may not be valid for some applications
      assertEquals("+02:00", parisRules.getOffset(parisDstOverlap).toString());
    }

    @DisplayName("Sao Paulo region has no DST")
    @Test
    public void regionSaoPaulo() {
      Instant winter = Instant.parse("2021-07-31T00:00:00Z");
      assertTrue(ZoneId.of("America/Sao_Paulo").getRules().getDaylightSavings(winter).isZero());

      // Brazil does not observe DST
      // However, the test fails because this version of Java (11.0.1) does not handle Sao Paulo region correctly
      Instant summer = Instant.parse("2021-12-31T00:00:00Z");
      assertTrue(ZoneId.of("America/Sao_Paulo").getRules().getDaylightSavings(summer).isZero());
    }
  }

  @Test
  public void otherRules() {
    //Other rules
    Instant winter = Instant.parse("2021-01-31T00:00:00Z");
    Instant summer = Instant.parse("2021-07-31T00:00:00Z");
    var rules = parisZoneId.getRules();
    Stream.of(winter, summer).forEach(instant -> {
      System.out.println(instant + "=" + rules.getStandardOffset(instant));
      System.out.println(instant + "=" + rules.getOffset(instant));
      System.out.println(rules.getDaylightSavings(instant));
      System.out.println(rules.nextTransition(instant));
      //System.out.println(rules.previousTransition(instant));
      rules.getTransitions().forEach(System.out::println);
      rules.getTransitionRules().forEach(System.out::println);
      System.out.println(rules.getValidOffsets(LocalDateTime.of(2021,1,1,0,0,0)));
      System.out.println(rules.getValidOffsets(LocalDateTime.of(2021,3,28,2,20,0)));
      System.out.println(rules.getValidOffsets(LocalDateTime.of(2021,10,31,2,20,0)));
      System.out.println("\n");
    });

    System.out.println(Duration.ofHours(2).addTo(LocalDateTime.of(2021,10,31,1,20,0)));
    System.out.println(Duration.ofDays(1).addTo(LocalDateTime.of(2021,10,31,1,20,0)));
    System.out.println(Duration.ofDays(1).addTo(LocalDateTime.of(2021,8,31,1,20,0)));
    ZoneId.getAvailableZoneIds().forEach(System.out::println);

    var now = Instant.now();
    Supplier<Stream<ZoneId>> zoneIds = () -> ZoneId.getAvailableZoneIds().stream().map(ZoneId::of);
    //var zoneIds = ZoneId.getAvailableZoneIds().stream().map(ZoneId::of);
    java.util.function.Function<ZoneId, Integer> offsetInSeconds = region -> region.getRules().getOffset(now).getTotalSeconds();
    int maxOffset = zoneIds.get().map(offsetInSeconds).max(Integer::compare).get();//.forEach(System.out::println);
    int minOffset = zoneIds.get().map(offsetInSeconds).min(Integer::compare).get();
    var maxOffsetRegion = zoneIds.get().filter(region -> offsetInSeconds.apply(region) == maxOffset).collect(Collectors.toList());
    var minOffsetRegion = zoneIds.get().filter(region -> offsetInSeconds.apply(region) == minOffset).collect(Collectors.toList());
    System.out.println(maxOffset/3600 + ":" + maxOffsetRegion);
    System.out.println(minOffset/3600 + ":" + minOffsetRegion);
  }

  @Test
  public void intervals() {
    var beforeDstGap = LocalDateTime.of(2021,3,28,1,20,0);
    assertEquals("2021-03-28T02:20", beforeDstGap.plusHours(1).toString());

    var parisDateTimeBeforeTransition = ZonedDateTime.of(beforeDstGap, parisZoneId);
    assertEquals("2021-03-28T01:20+01:00[Europe/Paris]", parisDateTimeBeforeTransition.toString());
    assertEquals("2021-03-28T00:20:00Z", parisDateTimeBeforeTransition.toInstant().toString());

    assertEquals("2021-03-28T03:20+02:00[Europe/Paris]", parisDateTimeBeforeTransition.plusHours(1).toString());
    assertEquals("2021-03-28T01:20:00Z", parisDateTimeBeforeTransition.plusHours(1).toInstant().toString());
  }

  @Test
  public void dob() {
    //who is older?
    //in general, to answer this question, people just consider the date without getting into more details
    var dob1 = LocalDate.of(1991,4,1);
    var dob2 = LocalDate.of(1991,3,1);
    assertTrue(dob1.isAfter(dob2));

    //Interestingly, if you live near the International Date Line (IDL) things get trickier
    //apparently, right now (2020-12-30T19:28:37Z), there are regions with offsets of -12 and +14, meaning that people born
    //on opposite sides of the IDL at the same time will have different dob!
    var now = Instant.parse("2020-12-30T19:28:37Z");
    assertEquals("14:[Pacific/Apia, Pacific/Kiritimati, Etc/GMT-14]", calculateOffset(now, s -> s.max(Integer::compare)));
    assertEquals("-12:[Etc/GMT+12]", calculateOffset(now, s -> s.min(Integer::compare)));
    assertEquals("2020-12-31T09:28:37+14:00[Pacific/Kiritimati]", ZonedDateTime.ofInstant(now, ZoneId.of("Pacific/Kiritimati")).toString());
    assertEquals("2020-12-30T07:28:37-12:00[Etc/GMT+12]", ZonedDateTime.ofInstant(now, ZoneId.of("Etc/GMT+12")).toString());
  }

  private String calculateOffset(Instant instant, Function<Stream<Integer>, Optional<Integer>> f) {
    Supplier<Stream<ZoneId>> zoneIds = () -> ZoneId.getAvailableZoneIds().stream().map(ZoneId::of);
    Function<ZoneId, Integer> offsetInSeconds = region -> region.getRules().getOffset(instant).getTotalSeconds();
    int offset = f.apply(zoneIds.get().map(offsetInSeconds)).get();
    var offsetRegion = zoneIds.get().filter(region -> offsetInSeconds.apply(region) == offset).collect(Collectors.toList());
    return (offset/3600 + ":" + offsetRegion);
  }

  @Test
  public void birthday() {
    //whose birthday is first in the year? to answer this question, no time zone needs to be considered
    var birthday1 = MonthDay.of(4, 10);
    var birthday2 = MonthDay.of(5, 10);
    assertTrue(birthday1.isBefore(birthday2));

    //however, the question: is today my birthday? does depend on the time zone
    var myBirthday = MonthDay.of(4, 10);
    var meInParis = ZonedDateTime.of(LocalDateTime.of(2020,4,10,20,0,0), ZoneId.of("Europe/Paris"));
    var myTwinInSydney = ZonedDateTime.ofInstant(meInParis.toInstant(), ZoneId.of("Australia/Sydney"));

    assertTrue(meInParis.getMonthValue() == myBirthday.getMonthValue() && meInParis.getDayOfMonth() == myBirthday.getDayOfMonth());
    assertFalse(myTwinInSydney.getMonthValue() == myBirthday.getMonthValue() && myTwinInSydney.getDayOfMonth() == myBirthday.getDayOfMonth());
  }

  @Test
  public void flightTickets() {
    //datetimes in tickets are always in local time
    var departureTime = LocalDateTime.of(2021,3,1,14,00,0);
    var arrivalTime = LocalDateTime.of(2021,3,1,16,10,0);

    //what's the flight duration?

    //wrong answer as LocalDateTime is not aware of time zone offsets
    assertEquals("PT2H10M", Duration.between(departureTime, arrivalTime).toString());

    //we need to take into account the time zones: departure from London, arrival to Paris
    var zonedDepartureTime = ZonedDateTime.of(departureTime, londonZoneId);
    var zonedArrivalTime = ZonedDateTime.of(arrivalTime, parisZoneId);

    //correct answer
    assertEquals("PT1H10M", Duration.between(zonedDepartureTime, zonedArrivalTime).toString());
  }

  @Test
  public void travelDuringDstChange() {
    //what if the flight happens when clocks move forward from winter to summer time
    var departureTime = LocalDateTime.of(2021,3,28,0,0,0);
    var arrivalTime = LocalDateTime.of(2021,3,28,3,10,0);

    //what's the flight duration?

    //wrong answer as LocalDateTime is not aware of DST rules
    assertEquals("PT3H10M", Duration.between(departureTime, arrivalTime).toString());

    //ZonedDateTime computes the DST though
    var zonedDepartureTime = ZonedDateTime.of(departureTime, londonZoneId);
    var zonedArrivalTime = ZonedDateTime.of(arrivalTime, parisZoneId);

    //correct answer
    assertEquals("PT1H10M", Duration.between(zonedDepartureTime, zonedArrivalTime).toString());
  }

  @Test
  public void expiryDate() {
    //is credit card expired?
    var card = YearMonth.of(2050, 3);
    assertTrue(!YearMonth.now().isAfter(card));

    /*
    Actually, the expiry date displayed on credit cards is not related to any time zone, therefore there is some
    ambiguity.
    Presumably, the bank will consider the time zone of the country where the card was issued. In that case, the
    above check should include that time zone
     */
    ZonedDateTime parisTime = ZonedDateTime.of(LocalDateTime.of(2021,1,31,15,0,0), ZoneId.of("Europe/Paris"));
    Clock parisClock = Clock.fixed(Instant.parse(parisTime.toInstant().toString()), ZoneId.of("Europe/Paris"));
    assertEquals("2021-01-31T15:00+01:00[Europe/Paris]", parisTime.toString());

    ZonedDateTime sydneyTime = parisTime.withZoneSameInstant(ZoneId.of("Australia/Sydney"));
    Clock sydneyClock = Clock.fixed(Instant.parse(sydneyTime.toInstant().toString()), ZoneId.of("Australia/Sydney"));
    assertEquals("2021-02-01T01:00+11:00[Australia/Sydney]", sydneyTime.toString());

    card = YearMonth.of(2021, 1);
    assertTrue(!YearMonth.now(parisClock).isAfter(card));
    assertTrue(YearMonth.now(sydneyClock).isAfter(card));
  }

  @Test
  public void nonExistentLocalDateTime() {

    //sys admin scheduling a task to run on servers in multiple regions
    //task must run before 3am local time and therefore is scheduled at 2:30

    //sys admin sets the time specifying a local date-time
    //the scheduler converts that time into the corresponding execution instant in each time zone
    var scheduledLocalDateTime = LocalDateTime.of(2021,3,28,2,30,0);

    //if we put that date into Paris time zone
    var parisZonedDateTime = ZonedDateTime.of(scheduledLocalDateTime, parisZoneId);

    //2021-03-28T02:30 is an invalid date in Paris time zone (clocks jump from 02:00+01:00 to 03:00+02:00)
    //Therefore Java falls back to summer time to represent the date in that time zone
    //As a result, the task runs after 3am!
    assertEquals("2021-03-28T03:30+02:00[Europe/Paris]", parisZonedDateTime.toString());

    //ALTERNATIVES
    //have the scheduler verify the validity of the local date time in each time zone so that
    //the user gets alerted

    //There is no valid offset for the gap time as the clock jumps
    assertTrue(parisRules.getValidOffsets(parisDstGap).isEmpty());

    assertThrows(IllegalArgumentException.class, () -> {
      if(parisRules.getValidOffsets(scheduledLocalDateTime).isEmpty()) throw new IllegalArgumentException();
    });
  }

  @Test
  public void duplicateLocalDateTime() {

    //similar to the previous one but now the local date time specified can be mapped to 2 different instants
    var scheduledLocalDateTime = LocalDateTime.of(2021,10,31,2,30,0);

    //if we put that date into Paris time zone
    var parisZonedDateTime = ZonedDateTime.of(scheduledLocalDateTime, parisZoneId);

    //2021-03-28T02:30 can be mapped to 2 instants in Paris time zone (clocks are set back from 03:00+02:00 to 02:00+01:00)
    //Again Java falls back to summer time to represent the date in that time zone
    //As a result, the task runs the first time the clock shows 02:30
    assertEquals("2021-10-31T02:30+02:00[Europe/Paris]", parisZonedDateTime.toString());

    //ALTERNATIVES: if instead we need the task to run the second time the clock shows 02:30, we can

    //1. configure the application to do that
    assertEquals("2021-10-31T02:30+01:00[Europe/Paris]", parisZonedDateTime.withLaterOffsetAtOverlap().toString());

    //2. have the scheduler verify the validity of the local date time in each time zone so that
    // the user gets alerted
    //There are 2 offsets for the overlap time as the clock moves twice through that time
    assertArrayEquals(parisRules.getValidOffsets(parisDstOverlap).stream().map(ZoneOffset::toString).toArray(), new String[]{"+02:00","+01:00"});

    assertThrows(IllegalArgumentException.class, () -> {
      if(parisRules.getValidOffsets(scheduledLocalDateTime).size() == 2) throw new IllegalArgumentException();
    });

    //To detect both gaps and overlaps, we can do
    assertThrows(IllegalArgumentException.class, () -> {
      if(parisRules.getValidOffsets(scheduledLocalDateTime).size() != 1) throw new IllegalArgumentException();
    });
  }

  @Test
  public void taskSchedulerWithDefaultDstBehaviour() {
    //implementation of the scheduler discussed above (what is the execution instant in each region corresponding to the same local time?)
    var scheduledLocalDateTime = LocalDateTime.of(2021,10,31,2,30,0);
    var parisScheduledDateTime = ZonedDateTime.of(scheduledLocalDateTime, parisZoneId);

    assertEquals(
      "[2021-10-31T02:30-07:00[America/Los_Angeles], 2021-10-31T02:30-03:00[America/Sao_Paulo], 2021-10-31T02:30+02:00[Europe/Paris], 2021-10-31T02:30+09:00[Asia/Tokyo]]",
      Arrays.toString(Stream.of("America/Los_Angeles", "America/Sao_Paulo", "Europe/Paris", "Asia/Tokyo").map(region -> parisScheduledDateTime.withZoneSameLocal(ZoneId.of(region)).toString()).toArray())
    );

    //All instants are different
    assertEquals(4, Stream.of("America/Los_Angeles", "America/Sao_Paulo", "Europe/Paris", "Asia/Tokyo")
      .map(region -> parisScheduledDateTime.withZoneSameLocal(ZoneId.of(region)).toInstant()).distinct().toArray().length);
  }

  @Test
  public void sportEvent() {
    //what time is the game in different countries? (same instant expressed in different local time lines)

    var gameLocalTime = ZonedDateTime.of(LocalDateTime.of(2021, 3, 27, 18, 30, 0), ZoneId.of("America/Los_Angeles"));

    assertEquals(
      "[2021-03-27T18:30-07:00[America/Los_Angeles], 2021-03-27T22:30-03:00[America/Sao_Paulo], 2021-03-28T03:30+02:00[Europe/Paris], 2021-03-28T10:30+09:00[Asia/Tokyo]]",
      Arrays.toString(Stream.of("America/Los_Angeles", "America/Sao_Paulo", "Europe/Paris", "Asia/Tokyo").map(region -> gameLocalTime.withZoneSameInstant(ZoneId.of(region)).toString()).toArray())
    );

    //All instants are the same
    assertTrue(Stream.of("America/Los_Angeles", "America/Sao_Paulo", "Europe/Paris", "Asia/Tokyo")
      .map(region -> gameLocalTime.withZoneSameInstant(ZoneId.of(region)).toInstant())
      .allMatch(gameLocalTime.toInstant()::equals));

  }

  @Test
  public void voucherRedemption() {
    //use zoned date time because voucher can be used in different time zones by the website user
    {
      var voucherIssueDate = ZonedDateTime.of(LocalDateTime.of(2021, 3, 27, 10, 0, 0), parisZoneId);
      var voucherExpiryDate = voucherIssueDate.plusDays(1);
      assertEquals("2021-03-28T10:00+02:00[Europe/Paris]", voucherExpiryDate.toString());
      assertEquals(23, Duration.between(voucherIssueDate, voucherExpiryDate).toHours());
    }

    {
      var voucherIssueDate = ZonedDateTime.of(LocalDateTime.of(2021, 3, 27, 10, 0, 0), parisZoneId);
      var voucherExpiryDate = voucherIssueDate.plusHours(24);
      assertEquals("2021-03-28T11:00+02:00[Europe/Paris]", voucherExpiryDate.toString());
      assertEquals(24, Duration.between(voucherIssueDate, voucherExpiryDate).toHours());
    }
  }

  public String[] timeZoneHistory(ZoneId zoneId) {
    var _1900 = ZonedDateTime.of(LocalDateTime.of(1900, 1, 1, 0, 0, 0), zoneId);
    return IntStream.range(0, 121).mapToObj(i -> (1900 + i) + ":" + zoneId.getRules().getStandardOffset(_1900.plusYears(i).toInstant()).toString()).toArray(String[]::new);
  }

  public List<String> dstHistory(ZoneId zoneId) {
    return zoneId.getRules().getTransitions().stream().map(ZoneOffsetTransition::toString).collect(Collectors.toList());
  }

  @Test
  public void timeZoneHistoryParis() {
    System.out.println(String.join("\n", timeZoneHistory(parisZoneId)));
  }

  @Test
  public void dstHistoryParis() {
    System.out.println(String.join("\n", dstHistory(parisZoneId)));
  }

  @Test
  public void timeZoneHistoryLondon() {
    System.out.println(String.join("\n", timeZoneHistory(londonZoneId)));
  }

  @Test
  public void dstHistoryLondon() {
    System.out.println(String.join("\n", dstHistory(londonZoneId)));
  }






}
