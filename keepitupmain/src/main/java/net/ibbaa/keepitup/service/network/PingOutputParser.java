/*
 * Copyright (c) 2025 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service.network;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingOutputParser {

    private final static Pattern PING = Pattern.compile("([0-9]+)(\\s+)(bytes)(.*(time)([=<]))([0-9]+(\\.[0-9]+)?)( ?)(\\S+).*");
    private final static Pattern SUMMARY = Pattern.compile("(([0-9]+)\\s[\\w|\\s]+),\\s(([0-9]+)\\s[\\w|\\s]+),\\s(([0-9]+(\\.[0-9]+)?)%\\s[\\w|\\s]+),.*(time)\\s(.*)");

    private boolean validInput;
    private int packetsTransmitted;
    private int packetsReceived;
    private double packetLoss;
    private int bytesReceived;
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

    public int getBytesReceived() {
        return bytesReceived;
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
            if (lines.length == 0) {
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
            Log.d(PingOutputParser.class.getName(), "Parsing was successful.");
        } catch (Exception exc) {
            Log.e(PingOutputParser.class.getName(), "Ping output parsing error", exc);
            setInvalid();
        }
    }

    private boolean parseTimeValues(String[] lines) {
        Log.d(PingOutputParser.class.getName(), "parseTimeValues");
        int timesParsed = 0;
        double timeSum = 0.0;
        bytesReceived = 0;
        for (String line : lines) {
            line = line.trim();
            Matcher matcher = PING.matcher(line);
            if (matcher.matches()) {
                Log.d(PingOutputParser.class.getName(), "Matching time value line: " + line);
                if (matcher.groupCount() < 7) {
                    Log.d(PingOutputParser.class.getName(), "Parsing error. Group count is invalid for a time value line.");
                    setInvalid();
                    return false;
                }
                if (bytesReceived <= 0) {
                    String bytesReceivedValue = matcher.group(1);
                    String bytesString = matcher.group(3);
                    Log.d(PingOutputParser.class.getName(), "Parsed bytes received value: " + bytesReceivedValue);
                    Log.d(PingOutputParser.class.getName(), "Parsed bytes string: " + bytesString);
                    if (!"bytes".equals(bytesString)) {
                        Log.d(PingOutputParser.class.getName(), "Parsing error. Parsed bytes string is not \"bytes\"");
                        setInvalid();
                        return false;
                    }
                    if (!NumberUtil.isValidIntValue(bytesReceivedValue)) {
                        Log.d(PingOutputParser.class.getName(), "Bytes received value " + bytesReceivedValue + " is not a valid int.");
                        setInvalid();
                        return false;
                    }
                    bytesReceived = NumberUtil.getIntValue(bytesReceivedValue, 0);
                }
                String equalSign = matcher.group(6);
                String time = matcher.group(7);
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
