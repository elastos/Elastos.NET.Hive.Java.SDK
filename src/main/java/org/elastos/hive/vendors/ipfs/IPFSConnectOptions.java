/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.HiveConnectOptions;

public class IPFSConnectOptions extends HiveConnectOptions {
    private final IPFSRpcNode[] hiveRpcNodes;

    IPFSConnectOptions(){
        this(new Builder());
    }

    IPFSConnectOptions(Builder builder){
        this.hiveRpcNodes = builder.hiveRpcNodes;
        setBackendType(HiveBackendType.HiveBackendType_IPFS);
    }

    IPFSRpcNode[] getHiveRpcNodes() {
        return hiveRpcNodes;
    }

    public static class Builder{
        private IPFSRpcNode[] hiveRpcNodes ;

        public Builder(){
            IPFSRpcNode node = new IPFSRpcNode("127.0.0.1",5001);
            hiveRpcNodes = new IPFSRpcNode[]{node};
        }
        Builder(IPFSConnectOptions ipfsConnectOptions){
            this.hiveRpcNodes = ipfsConnectOptions.hiveRpcNodes;
        }

        public Builder ipfsRPCNodes(IPFSRpcNode[] ipfsRpcNodes){
            this.hiveRpcNodes = ipfsRpcNodes ;
            return this ;
        }

        public IPFSConnectOptions build(){
            return new IPFSConnectOptions(this);
        }
    }
}
