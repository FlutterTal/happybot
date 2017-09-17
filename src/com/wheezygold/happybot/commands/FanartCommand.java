package com.wheezygold.happybot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.wheezygold.happybot.Util.C;
import com.wheezygold.happybot.Util.Roles;

public class FanartCommand extends Command {

    public FanartCommand() {
        this.name = "fanart";
        this.help = "Puts a no-fanart message to a player!";
        this.arguments = "<user>";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getGuild(), e.getMember(), Roles.HELPER)) {
            if (e.getMessage().getMentionedUsers().size() == 1) {
                e.getMessage().delete().reason("Auto Command Deletion").queue();
                String msg = C.getMemberEvent(e).getAsMention() + ", please post only fanart in " + e.getGuild().getTextChannelById("337689640888827905").getAsMention();
                e.replySuccess(msg);
                C.getMemberEvent(e).getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(msg).queue());
            } else {
                e.replyError("Please (only) mention one user!");
            }
        } else {
            e.replyError(C.permMsg(Roles.HELPER));
        }
    }
}