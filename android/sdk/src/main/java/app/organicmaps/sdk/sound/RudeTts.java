package app.organicmaps.sdk.sound;

import androidx.annotation.NonNull;
import app.organicmaps.sdk.util.Config;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ThreadLocalRandom;

public final class RudeTts
{
  private static final Pattern FEET =
      Pattern.compile("\\b(\\d+)\\s+feet\\b", Pattern.CASE_INSENSITIVE);

  private static final String[] LEFT = {
      "Turn left, you fucking muppet.",
      "Left turn. Try not to miss the bastard.",
      "Turn left, for fuck's sake.",
      "Take the left, you absolute bellend."
  };

  private static final String[] RIGHT = {
      "Turn right, you fucking muppet.",
      "Right turn. Pay attention this time.",
      "Turn right, for fuck's sake.",
      "Take the right, you absolute bellend."
  };

  private static final String[] RECALC = {
      "Right, you've fucked it. Recalculating.",
      "For fuck's sake. Recalculating.",
      "You've ignored me, so I'm fixing your mess.",
      "Brilliant. Now I have to find another way."
  };

  private static final String[] WRONG = {
      "You're on the wrong fucking route.",
      "Wrong route, you absolute muppet.",
      "Nope. That's completely fucking wrong.",
      "Congratulations, you've gone the wrong way."
  };

  private static final String[] DEST = {
      "You're nearly there. Miraculously.",
      "Destination approaching. Don't fuck it up now.",
      "Almost there, you magnificent fucking disaster.",
      "You actually made it."
  };

  private static final String[] KEEP_LEFT = {
      "Keep left, you muppet.",
      "Keep left. It's not fucking difficult."
  };

  private static final String[] KEEP_RIGHT = {
      "Keep right, you muppet.",
      "Keep right. Pay fucking attention."
  };

  private static final String[] UTURN = {
      "Make a U-turn, you absolute bellend.",
      "Turn around. Yes, you've fucked it."
  };

  private static final String[] ROUNDABOUT = {
      "Roundabout ahead. Try counting this time.",
      "Approaching a roundabout. Don't fuck it up."
  };

  private RudeTts() {}

  @NonNull
  public static String transform(@NonNull String text)
  {
    if (!Config.TTS.isRudeEnabled())
      return text;

    if (!isEnglish())
      return text;

    String out = convertFeetToYards(text);
    String lower = out.toLowerCase(Locale.US);
    String extra = null;

    if (lower.contains("recalculat"))
      extra = pick(RECALC);
    else if (lower.contains("wrong route") || lower.contains("route has changed"))
      extra = pick(WRONG);
    else if (lower.contains("destination") ||
             lower.contains("you have arrived") ||
             lower.contains("arrived"))
      extra = pick(DEST);
    else if (lower.contains("u-turn") || lower.contains("u turn"))
      extra = pick(UTURN);
    else if (lower.contains("roundabout"))
      extra = pick(ROUNDABOUT);
    else if (lower.contains("keep left"))
      extra = pick(KEEP_LEFT);
    else if (lower.contains("keep right"))
      extra = pick(KEEP_RIGHT);
    else if (lower.contains("turn left") || lower.contains("left"))
      extra = pick(LEFT);
    else if (lower.contains("turn right") || lower.contains("right"))
      extra = pick(RIGHT);

    return extra == null ? out : out + " " + extra;
  }

  private static boolean isEnglish()
  {
    String lang = Config.TTS.getLanguage();

    return lang == null ||
           lang.isEmpty() ||
           lang.toLowerCase(Locale.US).startsWith("en");
  }

  @NonNull
  private static String convertFeetToYards(@NonNull String text)
  {
    if (!Config.TTS.useYards())
      return text;

    Matcher m = FEET.matcher(text);
    StringBuffer b = new StringBuffer();

    while (m.find())
    {
      int feet = Integer.parseInt(m.group(1));
      int yards = Math.max(1, Math.round(feet / 3.0f));

      m.appendReplacement(b, yards + " yards");
    }

    m.appendTail(b);
    return b.toString();
  }

  private static String pick(String[] values)
  {
    return values[ThreadLocalRandom.current().nextInt(values.length)];
  }
}
