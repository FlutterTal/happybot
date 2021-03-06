package io.github.jroy.happybot.commands.warn;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import io.github.jroy.happybot.util.RuntimeEditor;
import io.github.jroy.happybot.sql.WarningManager;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WarningsCommand extends Command {

    private WarningManager warningManager;

    public WarningsCommand(WarningManager warningManager) {
        this.name = "warnings";
        this.aliases = new String[]{"warns"};
        this.arguments = "<user>";
        this.help = "Warns the target user.";
        this.guildOnly = true;
        this.category = new Category("Staff Tools");
        this.warningManager = warningManager;
    }

    @Override
    protected void execute(CommandEvent e) {
        if (C.hasRole(e.getMember(), Roles.HELPER) || RuntimeEditor.isPermittingWarningExposement()) {
            if (!C.containsMention(e)) {
                try {
                    WarningToken token = grabWarnings(e.getAuthor());
                    if (token.getWarnings() == 0) {
                        e.replyError("**Correct Usage:** ^" + name + " **<user>**");
                        return;
                    }
                    e.reply(token.getBuilder().toString());
                    return;
                } catch (SQLException e1) {
                    e.replyError("**Correct Usage:** ^" + name + " **<user>**");
                }
                e.replyError("**Correct Usage:** ^" + name + " **<user>**");
                return;
            }

            String channelId = e.getChannel().getId();

            if ((Channels.GENERAL.getId().equalsIgnoreCase(channelId) || Channels.RANDOM.getId().equalsIgnoreCase(channelId) || Channels.GAMBLE.getId().equalsIgnoreCase(channelId) || Channels.MUSIC_REQUEST.getId().equalsIgnoreCase(channelId)) && !C.hasRole(e.getMember(), Roles.SUPER_ADMIN) && !RuntimeEditor.isPermittingWarningExposement()) {
                e.reply("Please use a staff channel to view user warnings...");
                return;
            }

            try {
                WarningToken token = grabWarnings(C.getMentionedMember(e).getUser());
                if (token.getWarnings() == 0) {
                    e.replyError("Target User has no warnings.");
                    return;
                }
                e.reply(token.getBuilder().toString());
            } catch (SQLException e1) {
                e.replyError("Oof Error: " + e1.getMessage());
            }
        } else {
            e.reply(C.permMsg(Roles.HELPER));
        }
    }

    private WarningToken grabWarnings(User user) throws SQLException {
        ResultSet resultSet = warningManager.fetchWarnings(user.getId());
        StringBuilder builder = new StringBuilder();
        builder.append(user.getName()).append("'s Warnings\n");
        int warnings = 0;
        while (resultSet.next()) {
            Member staffMem = C.getGuild().getMemberById(resultSet.getString("staffid"));
            if (staffMem != null) {
                builder.append("#").append(resultSet.getString("id")).append(" ")
                        .append(C.bold(staffMem.getUser().getName() + "#" + staffMem.getUser().getDiscriminator())).append(" - ").append(C.bold(resultSet.getString("reason")))
                        .append(" (").append(resultSet.getTimestamp("time").toString()).append(")")
                        .append("\n");
                warnings++;
            }
        }
        return new WarningToken(builder, warnings);
    }

    private class WarningToken {

        private StringBuilder builder;
        private int warnings;

        WarningToken(StringBuilder builder, int warnings) {
            this.builder = builder;
            this.warnings = warnings;
        }

        private StringBuilder getBuilder() {
            return builder;
        }

        private int getWarnings() {
            return warnings;
        }
    }

}
