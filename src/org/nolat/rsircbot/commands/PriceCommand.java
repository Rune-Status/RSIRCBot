package org.nolat.rsircbot.commands;

import org.nolat.rsircbot.RSIRCBot;
import org.nolat.rsircbot.data.ItemSearch;
import org.nolat.rsircbot.data.LookupException;
import org.nolat.rsircbot.data.json.ItemData;

public class PriceCommand extends Command {

    public PriceCommand() {
        super("price");
        addAlternativeCommand("pc");
        setArgString("<item name>");
        setHelpMessage("Retrieves the current market value of an item");
    }

    @Override
    public void executeCommand(RSIRCBot bot, String channel, String executor, String message) {
        if (message.length() == "!price".length()) {
            bot.sendMessage(channel, executor, getUsageString(), this);
        } else {
            String item = message.substring("!price ".length(), message.length()).replaceAll(" ", "_");

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
                    bot.sendMessage(
                            channel, executor,
                            matchedItem.getName() + " costs " + matchedItem.getPriceString() + " gp. (Low: "
                                    + matchedItem.getRecentLowString() + "; High: " + matchedItem.getRecentHighString()
                                    + ")", this);
                }
            }
        }
    }

}
