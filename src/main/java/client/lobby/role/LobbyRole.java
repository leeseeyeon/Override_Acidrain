package client.lobby.role;

import client.lobby.LobbyModel;
import client.network.*;

public interface LobbyRole {
    String getActionButtonText(LobbyModel model);

    boolean isActionButtonEnabled(LobbyModel model);

    void handleActionButtonClick(ServerConnection conn, LobbyModel model);

    boolean canEditOptions();
}