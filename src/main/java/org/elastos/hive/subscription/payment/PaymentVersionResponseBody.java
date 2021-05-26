package org.elastos.hive.subscription.payment;

import org.elastos.hive.connection.HiveResponseBody;

class PaymentVersionResponseBody extends HiveResponseBody {
    private String version;

    public String getVersion() {
        return this.version;
    }
}