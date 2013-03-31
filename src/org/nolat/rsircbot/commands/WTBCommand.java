package org.nolat.rsircbot.commands;

import java.util.List;

import org.nolat.rsircbot.RSIRCBot;
import org.nolat.rsircbot.data.ItemSearch;
import org.nolat.rsircbot.data.LookupException;
import org.nolat.rsircbot.data.json.ItemData;
import org.nolat.rsircbot.data.json.OfferData;
import org.nolat.rsircbot.tools.TimeUtils;

public class WTBCommand extends Command {

    public WTBCommand() {
        super("wtb");
        setArgString("<item name>");
        setHelpMessage("Returns a list of people selling the specified item");
    }

    @Override
    public void executeCommand(RSIRCBot bot, String channel, String executor, String message) {
        if (message.length() == "!wtb".length()) {
            bot.sendMessage(channel, executor, getUsageString(), this);
        } else {
            String item = message.substring("!wtb ".length(), message.length()).replaceAll(" ", "_");

            ItemSearch results = null;
            try {
                results = new ItemSearch(item);
            } catch (LookupException e) {
                bot.sendMessage(channel, executor, e.getMessage(), this);
            }

            if (results != null) {
                ItemData matchedItem = results.getMatchedItem();
                if (matchedItem == null) {
                    bot.sendMessage(channel, executor,
                            "Search was too broad, try one of these: " + results.getSuggestionString(), this);
                } else {
                    List<OfferData> offers = matchedItem.getRecentSellers(12);
                    if (offers.size() > 1) {
                        bot.sendMessage(channel, executor,
                                "--Found " + offers.size() + " people selling " + matchedItem.getName()
                                + " in the last 12 hours. (Showing most recent)--", this);
                        for (int i = 0; i < Math.min(5, offers.size()); i++) {
                            bot.sendMessage(
                                    channel, executor,
                                    "'" + offers.get(i).getRSN() + "' - selling " + offers.get(i).getQuantityString()
                                    + " for "
                                    + offers.get(i).getPriceString() + " gp ea. As of "
                                    + TimeUtils.millisToLongDHMS(offers.get(i).msSincePosted()) + " ago.", this);
                        }
                    } else {
                        bot.sendMessage(channel, executor, "Could not find anyone selling " + matchedItem.getName(),
                                this);
                    }
                }
            }
        }
    }

}
