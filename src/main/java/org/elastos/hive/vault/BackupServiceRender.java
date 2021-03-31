package org.elastos.hive.vault;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.hive.Vault;
import org.elastos.hive.auth.BackupRemoteResolver;
import org.elastos.hive.auth.LocalResolver;
import org.elastos.hive.auth.TokenResolver;
import org.elastos.hive.network.request.BackupRestoreRequestBody;
import org.elastos.hive.network.request.BackupSaveRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.service.BackupContext;
import org.elastos.hive.service.BackupService;

class BackupServiceRender extends HiveVaultRender implements BackupService, HttpExceptionHandler {
    private BackupContext backupContext;
    private TokenResolver tokenResolver;

    public BackupServiceRender(Vault vault) {
        super(vault);
    }

    @Override
    public CompletableFuture<Void> setupContext(BackupContext backupContext) {
        this.backupContext = backupContext;
        this.tokenResolver = new LocalResolver(
                getVault().getAppContext().getUserDid(),
                getVault().getAppContext().getProviderAddress(),
                LocalResolver.TYPE_BACKUP_CREDENTIAL,
                getVault().getAppContext().getAppContextProvider().getLocalDataDir());
        this.tokenResolver.setNextResolver(new BackupRemoteResolver(
                getVault().getAppContext(),
                backupContext,
                backupContext.getParameter("targetDid"),
                backupContext.getParameter("targetHost")));
        return null;
    }

    @Override
    public CompletableFuture<Void> startBackup() {
        return CompletableFuture.runAsync(() -> {
            try {
                HiveResponseBody.validateBody(
                        getConnectionManager().getBackupApi()
                                .saveToNode(new BackupSaveRequestBody(tokenResolver.getToken().getAccessToken()))
                                .execute()
                                .body());
            } catch (Exception e) {
                throw new CompletionException(convertException(e));
            }
        });
    }

    @Override
    public CompletableFuture<Void> stopBackup() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Void> restoreFrom() {
        return CompletableFuture.runAsync(() -> {
            try {
                HiveResponseBody.validateBody(
                        getConnectionManager().getBackupApi()
                                .restoreFromNode(new BackupRestoreRequestBody(
                                        tokenResolver.getToken().getAccessToken()))
                                .execute()
                                .body());
            } catch (Exception e) {
                throw new CompletionException(convertException(e));
            }
        });
    }

    @Override
    public CompletableFuture<Void> stopRestore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<BackupResult> checkResult() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return HiveResponseBody.validateBody(
                        getConnectionManager().getBackupApi()
                                .getState()
                                .execute()
                                .body()).getStatusResult();
            } catch (Exception e) {
                throw new CompletionException(convertException(e));
            }
        });
    }
}
