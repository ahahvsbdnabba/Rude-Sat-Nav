package app.organicmaps.sdk.sound;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.organicmaps.sdk.util.Config;

public final class RudeDrivingMonitor
{
  private static final long SPEEDING_COOLDOWN_MS = 30_000L;
  private static final long BRAKING_COOLDOWN_MS = 20_000L;
  private static final long CORNER_COOLDOWN_MS = 30_000L;

  // Speeding must persist for this long before a comment is made.
  private static final long SPEEDING_DELAY_MS = 5_000L;

  // Approximate thresholds. Values are deliberately conservative to avoid
  // constant commentary from normal GPS fluctuations.
  private static final float HARSH_BRAKING_MPS2 = -4.0f;
  private static final float FAST_CORNER_SPEED_MPS = 13.9f; // ~50 km/h
  private static final float FAST_CORNER_TURN_DEGREES = 35.0f;

  private static Location sPreviousLocation;

  private static long sSpeedingSince = 0L;
  private static long sLastSpeedingComment = 0L;
  private static long sLastBrakingComment = 0L;
  private static long sLastCornerComment = 0L;

  private RudeDrivingMonitor()
  {
  }

  public static synchronized void reset()
  {
    sPreviousLocation = null;
    sSpeedingSince = 0L;
    sLastSpeedingComment = 0L;
    sLastBrakingComment = 0L;
    sLastCornerComment = 0L;
  }

  /**
   * Call this whenever a new GPS location is received while route following
   * is active.
   *
   * @param location current GPS location
   * @param speedLimitMps current route speed limit in metres/second,
   *                      or <= 0 when unavailable
   * @return a rude driving comment, or an empty string
   */
  @NonNull
  public static synchronized String onLocation(
      @NonNull Location location,
      float speedLimitMps)
  {
    if (!Config.TTS.isRudeEnabled())
    {
      reset();
      return "";
    }

    if (location.getSpeed() < 0.5f)
    {
      sPreviousLocation = new Location(location);
      sSpeedingSince = 0L;
      return "";
    }

    final long now = System.currentTimeMillis();

    String comment = "";

    if (speedLimitMps > 0.0f)
      comment = checkSpeeding(location, speedLimitMps, now);

    if (!comment.isEmpty())
    {
      sPreviousLocation = new Location(location);
      return comment;
    }

    if (sPreviousLocation != null)
    {
      comment = checkHarshBraking(location, now);

      if (!comment.isEmpty())
      {
        sPreviousLocation = new Location(location);
        return comment;
      }

      comment = checkFastCorner(location, now);

      if (!comment.isEmpty())
      {
        sPreviousLocation = new Location(location);
        return comment;
      }
    }

    sPreviousLocation = new Location(location);

    return "";
  }

  @NonNull
  private static String checkSpeeding(
      @NonNull Location location,
      float speedLimitMps,
      long now)
  {
    /*
     * Allow approximately 10% plus 2 km/h over the route speed limit before
     * considering the driver to be speeding. This prevents GPS noise from
     * constantly triggering the voice.
     */
    final float tolerance = Math.max(
        1.0f,
        speedLimitMps * 0.10f);

    final boolean speeding =
        location.getSpeed() > speedLimitMps + tolerance;

    if (!speeding)
    {
      sSpeedingSince = 0L;
      return "";
    }

    if (sSpeedingSince == 0L)
      sSpeedingSince = now;

    if (now - sSpeedingSince < SPEEDING_DELAY_MS)
      return "";

    if (now - sLastSpeedingComment < SPEEDING_COOLDOWN_MS)
      return "";

    sLastSpeedingComment = now;

    return RudeTts.speedingComment();
  }

  @NonNull
  private static String checkHarshBraking(
      @NonNull Location location,
      long now)
  {
    if (now - sLastBrakingComment < BRAKING_COOLDOWN_MS)
      return "";

    final float oldSpeed = sPreviousLocation.getSpeed();
    final float newSpeed = location.getSpeed();

    if (oldSpeed < 3.0f || newSpeed < 0.5f)
      return "";

    final long dtMs =
        location.getTime() - sPreviousLocation.getTime();

    if (dtMs <= 0L || dtMs > 5_000L)
      return "";

    final float dt = dtMs / 1000.0f;
    final float acceleration = (newSpeed - oldSpeed) / dt;

    if (acceleration > HARSH_BRAKING_MPS2)
      return "";

    sLastBrakingComment = now;

    return RudeTts.harshBrakingComment();
  }

  @NonNull
  private static String checkFastCorner(
      @NonNull Location location,
      long now)
  {
    if (now - sLastCornerComment < CORNER_COOLDOWN_MS)
      return "";

    if (location.getSpeed() < FAST_CORNER_SPEED_MPS)
      return "";

    final float bearingChange =
        getBearingDifference(
            sPreviousLocation.getBearing(),
            location.getBearing());

    if (bearingChange < FAST_CORNER_TURN_DEGREES)
      return "";

    sLastCornerComment = now;

    return RudeTts.fastCornerComment();
  }

  private static float getBearingDifference(
      float first,
      float second)
  {
    float difference = Math.abs(first - second);

    if (difference > 180.0f)
      difference = 360.0f - difference;

    return difference;
  }
}
