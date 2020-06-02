/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2020 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.test.crypto;

import com.bitmark.cryptography.crypto.MerkleTree;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.Sha3512;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.cryptography.utils.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Hieu Pham
 * @since 6/2/20
 * Email: hieupham@bitmark.com
 */
public class MerkleTreeTest extends BaseCryptoTest {

    private static final String[] TEST_32 = {
            // (V) ba0 (base)
            "90e0d4154e0484cf808d964b09bb4ce9cd32b18625665d8afbe72e31a708b5b1",
            // (V) tx1
            "4d222dd8e3fc1e4808de06c1ce4e1837fee1386f00fda94cf8946a8b42ea2af5",
            // (V) tx2
            "0cd62ff72b30769f477665ce9c2689f91b3d457f922adee395338292d1bc5356",
            // (V) tx3
            "b7c1ae668ca0f4ad82a77d6b1495c9e94f03dd0a39e63ea11ef10c2bd0f39050",
            // (V) tx4
            "9129acb3b5514e742b1164d5245932620ceca5ddc3770346431810d0aa6103c4",
            // (V) tx5
            "01bf5d97d39d49b921d831252a779c46f8f6bc59048c3b6226913fa85e0c9df8",
            // (V) tx6
            "e5dfe780eb0aa859b1f91ada2edd509dd8b2b9294bd024d133da787d7345a5be",
            // (V) tx7
            "9e3e52026a528536ea050166123f08c4901a116f12f4e78e10a3bc1d9ac447ae",
            // (V) tx8
            "46afe1d6069aad653b31ac7d58f2be5ebd999b6895dcfdc1cc893659177187e7",
            // (V) tx9
            "bb83326a538a22f89fc012bb116c4d24b28fab947bab1e5940f98bf357b56f05",

            // ------------------------------------------------------------------
            // (V) R10 = ba0+tx1
            "83da1679f0d3ea0c60f4f0d13ea78c7d0a5e522767fda7df255e2edda17cd3ee",
            // (V) R11 = tx2+tx3
            "5aa2f672749e859d70932b7e74e5726e36de7f948175e72b5bb86a35ab919cc4",
            // (V) R12 = tx4+tx5
            "6015479bece401053ff72eedc0c61d70c6cfe5e596ba2040d56c0418c240eb9b",
            // (V) R13 = tx6+tx7
            "e4553e3af2e0d2be1a25048df18556a88ff74a4f492141500267b0a0e27403aa",
            // (V) R14 = tx8+tx9
            "7b6bc7712098dcccac05529ac3e55cf7c29adda9c8a357d52e47be44802483d9",

            // ------------------------------------------------------------------
            // (V) R15 = R10+R11
            "be660b063e866973f43311873a6e8ca729f2da1a42328cd9ea7d0f24562bd502",
            // (V) R16 = R12+R13
            "f1d00468a5302ab89166ef189cdf145df89897a62c69c857c465dbc722f79431",
            // (V) R17 = R14+R14 (duplicate because odd number)
            "2967ea1655a28fb370afccc26b9518587b0b28a9f888e4ee7c56867bb89405ea",

            // ------------------------------------------------------------------
            // (V) R18 = R15+R16
            "3c2551d0b7e974733d33feb87861bd1de5eaddc32e28ec20978eca3527f053a6",
            // (V) R19 = R17+R17 (duplicate because odd number)
            "2df96ac62efa5e8c18662a72ce4b0d1984ee3895323d68b256652d2ce3957f4c",

            // ------------------------------------------------------------------
            // (V) root = R18+R19
            "64b1b32765c5adb0ba2bbc9a425a6e2ba91ec9c48959171a9aa5162e5645b9e8"

    };

    private static final String[] TEST_64 = {
            // (V) ba0 (base)
            "b9c948b3953e810b4e5b7ae07e5627566b03347e09ea1186f284c548a4cefe9f8e87357b7914636cd3e9e6fa084b1c315d333e91262520a14b67e6542bbd6f6a",
            // (V) tx1
            "7b2b49958d050104a40ef622b8cd84457863413738d4f6c3bf7132143b74ece970d8ca80da1e0bc1eb271986509670353442a9ce6a93515332da9f1c72e65c37",
            // (V) tx2
            "d399efbd45481e907f1a4e5b21e4f302042fe2e49c555d23de005b3adf596371a0de2d3d5a2e97a6d3a6b8736084077da23c1c34cc797f4e32b74f49e7d6dd33",
            // (V) tx3
            "bcef0e1c3dc20d1eabf60cf09059ac43f747c23863a7b0465bccaadb7799782dc529f7d0e2a904fd17c11fbb36bc2a2a86be6a0fab8b402a10b9d705657db397",
            // (V) tx4
            "874c750ab6838ecc2d8756c146fcef8fc8d5fa965c0eb2b8878245a7b73890b6418b8867ce2a5cf319e1b84ad0ec2bb3d088aacd693f19f893ab9a2da9b8e273",
            // (V) tx5
            "67ab051434aab68c09bb72d7e65e9f41b46f02f3b893ece0b1b99137c92c0aecfb2a568b3e11c1829b15b413ec58de65e1c747e76e4f732ca9bccf303b261629",
            // (V) tx6
            "3991e37f60911f2a7706d28b4d1d9d4d419b6b293e4256b7e29e94d6a692316932650905e684d69aa12c58d65960a257d832c52c5853e88c9401cda897423844",
            // (V) tx7
            "77bfb8de7f740e3856cc57726e818a86d9a15b367788cc43962cc9da37a627655d9f35f138c7c1ef83a8397b0555fd234638625e50fe7a0d6a335f187bec200c",
            // (V) tx8
            "c3d114dd5280b29e8f922205d4c92259894a5a81d3f72ef60e62c2910f6de4dcd3899dbf0c57443c80082ca2d6d37a9388cfd76e5cd31fb3bd95043f07076cc4",
            // (V) tx9
            "eef32d6b696b2fe579cf5170ad52aad4194dc7f51c9ae738fecd5e46161b500ca5d3a66f837b75b30929c10538ff3014c067c83dd01e993d94cca30eee0f54df",

            // ------------------------------------------------------------------
            // (V) R10 = ba0+tx1
            "7251f2f0922589abca19f23fc9340c139c67190a1ef2e2463b48795582caff25929c55de99efd2c5a44a62a70dd9b9cccffb066f7e93fe97a3badd8572d60619",
            // (V) R11 = tx2+tx3
            "1a279a6f99f9cbe8ec70c3c435e8df41a34a0a93689e51d057ece0955458755befaa408d75785b27daf0cad9e8354d463a474d28b3e34317fc19200072192463",
            // (V) R12 = tx4+tx5
            "91771f8bff4006f579586f8c957559e6c411fe11c48c55e162bebbcf0dc3a4c860f3e1f1f76bca3f1d1834e64414f4bd63c5bfa00ee7a00a248b07c215d43397",
            // (V) R13 = tx6+tx7
            "bc8263a8a429548162e89e26945ddd7455d7c737c5b639787ffe268af276e1b1d20beca548c6ccd37e7545c7bcf7c0e19caa7567ad0deace7afb5a040f896000",
            // (V) R14 = tx8+tx9
            "f5a37293d3bdd736234200bc5102afb8f175227173383275f899d683a28fef4f9eb6ed11a33b3b94de69b14803b099fd8b1fb9fee7708cb7b5c22cb02d24d72f",

            // ------------------------------------------------------------------
            // (V) R15 = R10+R11
            "47f7f3addf4d7fb826e797a3c7cafbf3110fdb6e32ffbf9603f571c1a94d49313647334724dcde5867349c03db1c232d214b7bbb39826d5758e38b691c259ac5",
            // (V) R16 = R12+R13
            "7f992d6c2e66a55b8c51434e48bfa3cb35efff196b5ad84ed9a2dede4dec58237b7b063f66a96b7678065a187862ad30ae35adb84ab0fc130305d73b22d7a6f0",
            // (V) R17 = R14+R14 (duplicate because odd number)
            "625297fabeb33209f5b6ac31bc0a8a3c332fb47d82f53582c2efe69644d2b7511e48b956117ffeb33ab38e82b350d26de3710994c9a854bab464c691fd45473c",

            // ------------------------------------------------------------------
            // (V) R18 = R15+R16
            "8ce171c5b544d81d3b6c9edde4dd820f01e9368d3bc699bc26cb8ec0536fbfb2b37a8bde1b775e17b070b210ba9781055764ef7274e0ab96c3b34568f2ea2ae1",
            // (V) R19 = R17+R17 (duplicate because odd number)
            "73a826f14992e66e1fedbc516a489edeff9b77a93cf223b285153665e266b563c5c3b1a2847dd45e489c693ba3a72451598934148e708a3dc362bf728b5f9690",
            // (V) root = R18+R19
            "2e54e5c7e2f6bc5bd375d70151fd99a8403c343a474a124cef97315be704b06681261c3fdced94463a635ee07a24e15307ea60069ffc53a1d7003f04f8b3ce89"

    };

    @Test
    public void testValidation() {
        assertThrows(
                ValidateException.class,
                () -> MerkleTree.buildTree(null, null)
        );
        assertThrows(ValidateException.class, () -> MerkleTree.buildTree(
                null,
                (b1, b2) -> new byte[0]
        ));
        assertThrows(
                ValidateException.class,
                () -> MerkleTree.buildTree(new byte[1][], null)
        );
        assertThrows(
                ValidateException.class,
                () -> MerkleTree.buildTree(
                        new byte[0][],
                        (b1, b2) -> new byte[0]
                )
        );
    }

    @Test
    public void test32() {
        testBuildTree(TEST_32, 256);
    }

    @Test
    public void test64() {
        testBuildTree(TEST_64, 512);
    }

    private void testBuildTree(String[] input, int length) {
        byte[][] expectedTree = new byte[input.length][];

        for (int i = 0; i < input.length; i++) {
            expectedTree[i] = HEX.decode(input[i]);
        }

        byte[][] ids = new byte[10][];
        System.arraycopy(expectedTree, 0, ids, 0, 10);
        byte[][] actualTree = MerkleTree.buildTree(
                ids,
                (b1, b2) -> {
                    byte[] b = ArrayUtils.concat(b1, b2);
                    return length == 256 ? Sha3256.hash(b) : Sha3512.hash(b);
                }
        );

        for (int i = 0; i < input.length; i++) {
            assertTrue(
                    Arrays.deepEquals(expectedTree, actualTree),
                    "failed at " + i
            );
        }
    }
}
