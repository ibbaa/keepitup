package de.ibba.keepitup.service.network;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.StringUtil;

public class PingOutputParser {

    private final static Pattern PING = Pattern.compile("(.*(time)([=<]))([0-9]+(\\.[0-9]+)?)( ?)(\\S+).*");
    private final static Pattern SUMMARY = Pattern.compile("(([0-9]+)\\s[\\w|\\s]+),\\s(([0-9]+)\\s[\\w|\\s]+),\\s(([0-9]+(\\.[0-9]+)?)%\\s[\\w|\\s]+),.*(time)\\s(.*)");

    private boolean validInput;
    private int packetsTransmitted;
    private int packetsReceived;
    private double packetLoss;
    private int validTimes;
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

    public int getValidTimes() {
        return validTimes;
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
            if (!parseFinalSummary(lines)) {
                Log.d(PingOutputParser.class.getName(), "Final summary cannot be parsed.");
                setInvalid();
                return;
            }
            validInput = true;
            Log.d(PingOutputParser.class.getName(), "Parsing was successul.");
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
            line = line.trim();
            Matcher matcher = PING.matcher(line);
            if (matcher.matches()) {
                Log.d(PingOutputParser.class.getName(), "Matching time value line: " + line);
                if (matcher.groupCount() < 4) {
                    Log.d(PingOutputParser.class.getName(), "Parsing error. Group count is invalid for a time value line.");
                    setInvalid();
                    return false;
                }
                String equalSign = matcher.group(3);
                String time = matcher.group(4);
                Log.d(PingOutputParser.class.getName(), "Parsed equal sign: " + equalSign);
                Log.d(PingOutputParser.class.getName(), "Parsed time value: " + time);
                if ("<".equals(equalSign)) {
                    timesParsed++;
                } else if ("=".equals(equalSign)) {
                    timesParsed++;
                    if (!NumberUtil.isValidDoubleValue(time)) {
                        Log.d(PingOutputParser.class.getName(), "Time value " + time + " is not a valid double.");
                        setInvalid();
                        return false;
                    }
                    timeSum += NumberUtil.getDoubleValue(time, 0.0);
                } else {
                    Log.d(PingOutputParser.class.getName(), "Equal sign " + equalSign + " is not valid.");
                    setInvalid();
                    return false;
                }
            }
        }
        validTimes = timesParsed;
        averageTime = (timesParsed > 0) ? timeSum / timesParsed : 0;
        return true;
    }

    private boolean parseFinalSummary(String[] lines) {
        Log.d(PingOutputParser.class.getName(), "parseFinalSummary");
        for (int ii = lines.length - 1; ii >= 0; ii--) {
            String line = lines[ii].trim();
            Matcher matcher = SUMMARY.matcher(line);
            if (matcher.matches()) {
                Log.d(PingOutputParser.class.getName(), "Matching final summary line: " + lines[ii]);
                if (matcher.groupCount() < 6) {
                    Log.d(PingOutputParser.class.getName(), "Parsing error. Group count is invalid for a final summary line.");
                    setInvalid();
                    return false;
                }
                String transmitted = matcher.group(2);
                String received = matcher.group(4);
                String loss = matcher.group(6);
                Log.d(PingOutputParser.class.getName(), "Parsed transmitted value: " + transmitted);
                Log.d(PingOutputParser.class.getName(), "Parsed received value: " + received);
                Log.d(PingOutputParser.class.getName(), "Parsed loss value: " + loss);
                if (!NumberUtil.isValidIntValue(transmitted)) {
                    Log.d(PingOutputParser.class.getName(), "Transmitted value " + transmitted + " is not a valid int.");
                    setInvalid();
                    return false;
                }
                if (!NumberUtil.isValidIntValue(received)) {
                    Log.d(PingOutputParser.class.getName(), "Received value " + received + " is not a valid int.");
                    setInvalid();
                    return false;
                }
                if (!NumberUtil.isValidDoubleValue(loss)) {
                    Log.d(PingOutputParser.class.getName(), "Loss value " + loss + " is not a valid double.");
                    setInvalid();
                    return false;
                }
                packetsTransmitted = NumberUtil.getIntValue(transmitted, 0);
                packetsReceived = NumberUtil.getIntValue(received, 0);
                packetLoss = NumberUtil.getDoubleValue(loss, 0.0);
                return true;
            }
        }
        return false;
    }

    private void setInvalid() {
        validInput = false;
        packetsTransmitted = -1;
        packetsReceived = -1;
        packetLoss = 0.0;
        validTimes = 0;
        averageTime = 0.0;
    }
}
