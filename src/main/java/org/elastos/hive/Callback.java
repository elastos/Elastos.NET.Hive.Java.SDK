package org.elastos.hive;

public interface Callback<T extends BaseItem> {
	public void onFailed(HiveException e);
	public void onSuccess(T object);
}
