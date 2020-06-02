/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2020 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto;

import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 6/2/20
 * Email: hieupham@bitmark.com
 */
public class MerkleTree {

    public static byte[][] buildTree(byte[][] txIds, CombineFunc combineFunc) {
        checkValid(
                () -> txIds != null && combineFunc != null,
                "txIds and combineFunc must not be null"
        );
        checkValid(() -> txIds.length > 0, "txIds must not be empty");
        int txsLength = txIds.length;
        int treeDeepLevel = 1;
        int n = txsLength;
        while (n > 1) {
            treeDeepLevel += n;
            n = (n + 1) / 2;
        }

        // add initial ids
        byte[][] tree = new byte[treeDeepLevel][];

        // copy txId to tree
        System.arraycopy(txIds, 0, tree, 0, txsLength);

        n = txsLength;
        int j = 0;
        int workLength = txsLength;
        while (workLength > 1) {
            for (int i = 0; i < workLength; i += 2) {
                int k = j + 1;
                if (i + 1 == workLength) {
                    k = j;
                }

                tree[n] = combineFunc.apply(tree[j], tree[k]);
                n += 1;
                j = k + 1;
            }

            workLength = (workLength + 1) / 2;
        }

        return tree;
    }

    public interface CombineFunc {
        byte[] apply(byte[] b1, byte[] b2);
    }
}
