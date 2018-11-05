package apiservice.params.query;

import com.google.gson.annotations.SerializedName;

import static cryptography.utils.Validator.checkValid;
import static cryptography.utils.Validator.checkValidString;

/**
 * @author Hieu Pham
 * @since 9/18/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class AssetQueryBuilder extends AbsQueryBuilder {

    private String registrant;

    @SerializedName("asset_ids")
    private String[] assetIds;

    private Integer limit = 100;

    public AssetQueryBuilder registrant(String registrant) {
        checkValidString(registrant);
        this.registrant = registrant;
        return this;
    }

    public AssetQueryBuilder assetIds(String[] assetIds) {
        checkValid(() -> assetIds != null && assetIds.length > 0, "Invalid asset ids");
        this.assetIds = assetIds;
        return this;
    }

    public AssetQueryBuilder limit(Integer limit) {
        checkValid(() -> limit != null && limit > 0, "Invalid limit value");
        this.limit = limit;
        return this;
    }
}
