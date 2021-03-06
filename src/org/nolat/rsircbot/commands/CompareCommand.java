package org.nolat.rsircbot.commands;

import java.io.IOException;

import org.nolat.rsircbot.RSIRCBot;
import org.nolat.rsircbot.data.HiscoreData;
import org.nolat.rsircbot.data.RankLevelXp;
import org.nolat.rsircbot.tools.Names;
import org.nolat.rsircbot.tools.RSFormatter;
import org.nolat.rsircbot.tools.Spellcheck;

public class CompareCommand extends Command {

    public CompareCommand() {
        super("compare");
        setArgString("<player1> <player2> <skillname>");
        setHelpMessage("Compare's a skill between two players.");
    }

    @Override
    public void executeCommand(RSIRCBot bot, String channel, String executor, String message) {
        String[] args = message.split(" "); //0 is the command
        if (args.length != 4) {
            bot.sendMessage(channel, executor, getUsageString(), this);
        } else {
            String username1 = Names.processUsername(bot, args[1], executor);
            String username2 = Names.processUsername(bot, args[2], executor);

            String skill = Spellcheck.correctSpelling(args[3]);

            try {
                HiscoreData user1hs = new HiscoreData(username1);
                HiscoreData user2hs = new HiscoreData(username2);

                RankLevelXp rlx1 = user1hs.getDataForSkill(skill);
                RankLevelXp rlx2 = user2hs.getDataForSkill(skill);
                if (rlx1 == null || rlx2 == null) {
                    bot.sendMessage(channel, executor, "Unable to parse " + skill + " information.", this);
                } else { //got value for both users
                    bot.sendMessage(channel, executor, username1 + " has level " + rlx1.getLevelString() + " " + skill
                            + " (XP: "
                            + rlx1.getXpString() + ") and " + username2 + " has level " + rlx2.getLevelString() + " "
                            + skill + " (XP: " + rlx2.getXpString() + ")", this);

                    if (rlx1.getXp() > rlx2.getXp()) {
                        int difference = rlx1.getXp() - rlx2.getXp();
                        bot.sendMessage(channel, executor, username1 + " has " + RSFormatter.format(difference)
                                + " more xp in "
                                + skill + " than " + username2, this);
                    } else if (rlx1.getXp() < rlx2.getXp()) {
                        int difference = rlx2.getXp() - rlx1.getXp();
                        bot.sendMessage(channel, executor, username2 + " has " + RSFormatter.format(difference)
                                + " more xp in "
                                + skill + " than " + username1, this);
                    } else {
                        bot.sendMessage(
                                channel,
                                executor,
                                username1 + " and " + username2 + " are tied in " + skill + ". (" + username1
                                + " is rank " + rlx1.getRank() + " and " + username2 + " is rank "
                                + rlx2.getRank() + ")",
                                this);
                    }
                }

            } catch (IOException e) {
                bot.sendMessage(channel, executor, "Unable to retrieve hiscore information", this);
            }

        }
    }
}
