package org.elastos.hive.vault.subscription;

import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.vault.subscription.VaultCreateResponseBody;
import org.elastos.hive.vault.subscription.VaultInfoResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

interface SubscriptionAPI {
	@POST("/api/v1/service/vault/create")
	Call<VaultCreateResponseBody> createVault();

	@POST("/api/v1/service/vault/freeze")
	Call<HiveResponseBody> freeze();

	@POST("/api/v1/service/vault/unfreeze")
	Call<HiveResponseBody> unfreeze();

	@POST("/api/v1/service/vault/remove")
	Call<HiveResponseBody> removeVault();

	@GET("/api/v1/service/vault")
	Call<VaultInfoResponseBody> getVaultInfo();

	@POST("/api/v1/service/vault_backup/create")
	Call<VaultCreateResponseBody> createBackupVault();

	@GET("/api/v1/service/vault_backup")
	Call<VaultInfoResponseBody> getBackupVaultInfo();
}