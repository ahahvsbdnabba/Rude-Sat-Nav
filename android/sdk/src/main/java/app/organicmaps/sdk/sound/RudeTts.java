package app.organicmaps.sdk.sound;

import androidx.annotation.NonNull;

import app.organicmaps.sdk.util.Config;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RudeTts
{
  private static final Pattern FEET =
      Pattern.compile("\\b(\\d+)\\s+feet\\b", Pattern.CASE_INSENSITIVE);

  private static String lastPhrase = "";

  private static final String[] LEFT = {
      "Turn left, you fucking muppet.",
      "Left turn. Try not to miss the bastard.",
      "Turn left, for fuck's sake.",
      "Take the left, you absolute bellend.",
      "Left. Yes, that one. Pay attention.",
      "Turn left, you magnificent fucking idiot.",
      "Left turn coming up. Don't cock this one up.",
      "Take the left, you useless navigator.",
      "Left turn. It really isn't that difficult.",
      "Turn left before you manage to fuck this up too."
  };

  private static final String[] RIGHT = {
      "Turn right, you fucking muppet.",
      "Right turn. Pay attention this time.",
      "Turn right, for fuck's sake.",
      "Take the right, you absolute bellend.",
      "Right. Yes, the other fucking direction.",
      "Turn right, you magnificent fucking idiot.",
      "Right turn coming up. Try not to fuck it up.",
      "Take the right, you useless navigator.",
      "Right turn. Concentrate for once.",
      "Turn right before you make another fucking mistake."
  };

  private static final String[] RECALC = {
      "Right, you've fucked it. Recalculating.",
      "For fuck's sake. Recalculating.",
      "You've ignored me, so I'm fixing your mess.",
      "Brilliant. Now I have to find another way.",
      "Well done. You've completely fucked the route.",
      "Recalculating because apparently you know better.",
      "You've gone off route. Again.",
      "Fantastic driving. Recalculating your disaster."
  };

  private static final String[] WRONG = {
      "You're on the wrong fucking route.",
      "Wrong route, you absolute muppet.",
      "Nope. That's completely fucking wrong.",
      "Congratulations, you've gone the wrong way.",
      "You've somehow managed to fuck up the route.",
      "That's not the route. What the fuck are you doing?",
      "Wrong way. Were you even listening?",
      "You've fucked it. Completely."
  };

  private static final String[] DEST = {
      "You're nearly there. Miraculously.",
      "Destination approaching. Don't fuck it up now.",
      "Almost there, you magnificent fucking disaster.",
      "You actually made it.",
      "Nearly there. Try not to screw up the final bit.",
      "Destination ahead. Somehow we've survived.",
      "Almost there. Keep your shit together.",
      "You've nearly managed to arrive. Incredible."
  };

  private static final String[] KEEP_LEFT = {
      "Keep left, you muppet.",
      "Keep left. It's not fucking difficult.",
      "Keep left, for fuck's sake.",
      "Stay left. Try paying attention.",
      "Keep left before you cock this up.",
      "Left lane. Yes, that one."
  };

  private static final String[] KEEP_RIGHT = {
      "Keep right, you muppet.",
      "Keep right. Pay fucking attention.",
      "Keep right, for fuck's sake.",
      "Stay right. Try not to fuck it up.",
      "Keep right before you cause another disaster.",
      "Right lane. Concentrate."
  };

  private static final String[] UTURN = {
      "Make a U-turn, you absolute bellend.",
      "Turn around. Yes, you've fucked it.",
      "U-turn. Because apparently the route was too difficult.",
      "Turn around before this gets any more fucking stupid.",
      "You've gone the wrong way. Turn around.",
      "Make a U-turn and pretend that never happened."
  };

  private static final String[] ROUNDABOUT = {
      "Roundabout ahead. Try counting this time.",
      "Approaching a roundabout. Don't fuck it up.",
      "Roundabout ahead. Concentrate.",
      "Roundabout. Pick the correct exit for once.",
      "Here comes a roundabout. Please don't embarrass yourself.",
      "Roundabout ahead. Try listening to the instructions."
  };

  private static final String[] SPEEDING = {
      "%s, slow the fuck down.",
      "%s, you're going too fucking fast.",
      "%s, ease off the accelerator before we end up somewhere stupid.",
      "%s, you're speeding. Back the fuck off.",
      "%s, perhaps try driving at a sensible fucking speed.",
      "%s, slow down. This isn't fucking Formula One.",
      "%s, you're pushing it now. Slow the fuck down.",
      "%s, take your foot off the fucking accelerator.",
      "%s, that's a bit fucking quick, don't you think?",
      "%s, slow down before the road bites back."
  };

  private static final String[] BRAKING = {
      "%s, what the fuck was that braking?",
      "%s, maybe try braking before the last fucking second.",
      "%s, that was some seriously fucking harsh braking.",
      "%s, smooth braking. We're not trying to throw people through the windscreen.",
      "%s, easy on the brakes, for fuck's sake.",
      "%s, you just slammed the brakes like an absolute lunatic.",
      "%s, perhaps give the brakes a little warning next time.",
      "%s, that braking was fucking brutal."
  };

  private static final String[] FAST_CORNER = {
      "%s, you're taking that fucking bend too fast.",
      "%s, slow the fuck down for this corner.",
      "%s, that's a bit fucking quick for a bend.",
      "%s, ease off. This corner isn't fucking flat.",
      "%s, brake before the corner, not halfway through it.",
      "%s, you're attacking that bend like a fucking rally driver.",
      "%s, slower through the corner, you absolute maniac.",
      "%s, maybe respect the fucking bend."
  };

  private static final String[] GENERAL_DRIVING = {
      "%s, what the fuck are you doing?",
      "%s, pay fucking attention.",
      "%s, concentrate on the road.",
      "%s, you're driving like a complete fucking muppet.",
      "%s, steady on.",
      "%s, perhaps try driving like a normal fucking person.",
      "%s, for fuck's sake, focus.",
      "%s, let's try not to turn this into a fucking disaster."
  };

  private RudeTts()
  {
  }

  @NonNull
  public static synchronized String transform(@NonNull String text)
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
    else if (lower.contains("wrong route") ||
             lower.contains("route has changed"))
      extra = pick(WRONG);
    else if (lower.contains("destination") ||
             lower.contains("you have arrived") ||
             lower.contains("arrived"))
      extra = pick(DEST);
    else if (lower.contains("u-turn") ||
             lower.contains("u turn"))
      extra = pick(UTURN);
    else if (lower.contains("roundabout"))
      extra = pick(ROUNDABOUT);
    else if (lower.contains("keep left"))
      extra = pick(KEEP_LEFT);
    else if (lower.contains("keep right"))
      extra = pick(KEEP_RIGHT);
    else if (lower.contains("turn left"))
      extra = pick(LEFT);
    else if (lower.contains("turn right"))
      extra = pick(RIGHT);
    else if (lower.contains("left"))
      extra = pick(LEFT);
    else if (lower.contains("right"))
      extra = pick(RIGHT);

    if (extra == null)
      return out;

    String driverName = Config.TTS.getDriverName();

    if (driverName != null && !driverName.trim().isEmpty())
      extra = driverName.trim() + ", " + extra;

    return out + " " + extra;
  }

  @NonNull
  public static synchronized String speedingComment()
  {
    if (!Config.TTS.isRudeEnabled() || !isEnglish())
      return "";

    return formatDriverName(pick(SPEEDING));
  }

  @NonNull
  public static synchronized String harshBrakingComment()
  {
    if (!Config.TTS.isRudeEnabled() || !isEnglish())
      return "";

    return formatDriverName(pick(BRAKING));
  }

  @NonNull
  public static synchronized String fastCornerComment()
  {
    if (!Config.TTS.isRudeEnabled() || !isEnglish())
      return "";

    return formatDriverName(pick(FAST_CORNER));
  }

  @NonNull
  public static synchronized String generalDrivingComment()
  {
    if (!Config.TTS.isRudeEnabled() || !isEnglish())
      return "";

    return formatDriverName(pick(GENERAL_DRIVING));
  }

  private static String formatDriverName(String phrase)
  {
    String name = Config.TTS.getDriverName();

    if (name == null || name.trim().isEmpty())
      return phrase.replace("%s, ", "");

    return String.format(Locale.US, phrase, name.trim());
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
    if (values.length == 1)
      return values[0];

    String result;

    do
    {
      result = values[ThreadLocalRandom.current().nextInt(values.length)];
    }
    while (result.equals(lastPhrase));

    lastPhrase = result;

    return result;
  }
}
