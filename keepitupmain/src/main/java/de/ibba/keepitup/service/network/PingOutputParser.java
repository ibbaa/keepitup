package de.ibba.keepitup.service.network;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class PingOutputParser {

    private final static Pattern PING = Pattern.compile("(.*(time)([=<]))([0-9]+(\\.[0-9]+)?)( ?)(\\S+).*");
    private final static Pattern SUMMARY = Pattern.compile("(([0-9]+)\\s[\\w|\\s]+),\\s(([0-9]+)\\s[\\w|\\s]+),.*(time)\\s(.*)");

    private boolean validInput;
    private int packetsTransmitted;
    private int packetsReceived;
    private double packetLoss;
    private double averageTime;

    public PingOutputParser() {
        setInvalid();
    }

    public void reset() {
        setInvalid();
    }

    public boolean isValidInput() {
        return validInput;
    }

    public int getPacketsTransmitted() {
        return packetsTransmitted;
    }

    public int getPacketsReceived() {
        return packetsReceived;
    }

    public double getPacketLoss() {
        return packetLoss;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public void parse(String pingOutput) {
        Log.d(PingOutputParser.class.getName(), "parse ping output: " + pingOutput);
        if (StringUtil.isEmpty(pingOutput)) {
            Log.d(PingOutputParser.class.getName(), "Ping output is empty.");
            setInvalid();
            return;
        }
        try {
            String[] lines = pingOutput.split("\\r?\\n|\\r");
            if (lines.length <= 0) {
                Log.d(PingOutputParser.class.getName(), "Ping output cannot be split to multiple lines.");
                setInvalid();
                return;
            }
            if (!parseTimeValues(lines)) {
                Log.d(PingOutputParser.class.getName(), "Time values cannot be parsed.");
                setInvalid();
                return;
            }
        } catch (Exception exc) {
            Log.e(PingOutputParser.class.getName(), "Ping output parsing error", exc);
            setInvalid();
        }
    }

    private boolean parseTimeValues(String[] lines) {
        Log.d(PingOutputParser.class.getName(), "parseTimeValues");
        int timesParsed = 0;
        double timeSum = 0.0;
        for (String line : lines) {
            Matcher matcher = PING.matcher(line);
            if (matcher.matches()) {
                if (matcher.groupCount() < 4) {
                    Log.d(PingOutputParser.class.getName(), "Parsing error. Line is a valid ping line but time cannot be parsed.");
                    setInvalid();
                    return false;
                }
                String equalSign = matcher.group(3);
                String number = matcher.group(4);
                Log.d(PingOutputParser.class.getName(), "Parsed equal sign: " + equalSign);
                Log.d(PingOutputParser.class.getName(), "Parsed number: " + number);
                if ("<".equals(equalSign)) {
                    timesParsed++;
                } else if ("=".equals(equalSign)) {
                    timesParsed++;
                    if (!NumberUtil.isValidDoubleValue(number)) {
                        Log.d(PingOutputParser.class.getName(), "Number " + number + " is not a valid double.");
                        setInvalid();
                        return false;
                    }
                    timeSum += NumberUtil.getDoubleValue(number, 0.0);
                } else {
                    Log.d(PingOutputParser.class.getName(), "Equal sign " + equalSign + " is not valid.");
                    setInvalid();
                    return false;
                }
            }
        }
        averageTime = timeSum / timesParsed;
        return true;
    }

    private void setInvalid() {
        validInput = false;
        packetsTransmitted = -1;
        packetsReceived = -1;
        packetLoss = 0.0;
        averageTime = 0.0;
    }
}
