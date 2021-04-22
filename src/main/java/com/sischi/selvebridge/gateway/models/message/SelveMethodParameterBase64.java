package com.sischi.selvebridge.gateway.models.message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sischi.selvebridge.util.Utils;

public class SelveMethodParameterBase64 extends SelveMethodParameter<String> {

    protected String binary;
    protected List<Integer> ids;
    
    public SelveMethodParameterBase64() {
        this.type = SelveMethodParameterType.BASE64;
    }

    public SelveMethodParameterBase64(String value) {
        this();
        this.value = value;
        setBinary(Utils.base64ToBinary(value));
        setIds(Utils.readIdsFromBinaryMask(getBinary()));
        setFriendlyValue(Utils.formatBinaryAsFriendlyValue(getBinary()));
        setFriendlyName(friendlyName);
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    protected void applyFriendlyNameAndValue() {
        setFriendlyValue(Utils.formatBinaryAsFriendlyValue(getBinary()));
        switch(getBinary() != null ? getBinary().length() : -1) {
            case 64:
                setFriendlyName("device id mask");
                break;
            case 32:
                setFriendlyName("group id mask");
                break;
        }
    }

    public static SelveMethodParameterBase64 ofBase64(String base64) {
        return new SelveMethodParameterBase64(base64);
    }

    public static SelveMethodParameterBase64 ofBinaryMask(String binary) {
        SelveMethodParameterBase64 param = new SelveMethodParameterBase64();
        param.setBinary(binary);
        param.setValue(Utils.binaryToBase64(binary));
        param.setIds(Utils.readIdsFromBinaryMask(param.getBinary()));
        param.setFriendlyValue(Utils.formatBinaryAsFriendlyValue(param.getBinary()));
        return param;
    }

    public static SelveMethodParameterBase64 ofIds(Integer... ids) {
        SelveMethodParameterBase64 param = new SelveMethodParameterBase64();
        param.setIds(Arrays.asList(ids));
        param.setBinary(Utils.writeIdsAsBinaryMask(param.getIds()));
        param.setValue(Utils.binaryToBase64(param.getBinary()));
        param.setFriendlyValue(Utils.formatBinaryAsFriendlyValue(param.getBinary()));
        return param;
    }

    public static SelveMethodParameterBase64 ofIdList(List<Integer> ids) {
        SelveMethodParameterBase64 param = new SelveMethodParameterBase64();
        param.setIds(ids);
        param.setBinary(Utils.writeIdsAsBinaryMask(param.getIds()));
        param.setValue(Utils.binaryToBase64(param.getBinary()));
        param.setFriendlyValue(Utils.formatBinaryAsFriendlyValue(param.getBinary()));
        return param;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +"=["+
                "type='"+ type +"', "+
                "value='"+ value.toString() +"', "+
                "binary='"+ binary +"', "+
                "ids='"+ (ids != null ? ids.stream().map(id -> id.toString()).collect(Collectors.joining(",")) : "null") +"', "+
                "friendlyName='"+ friendlyName +"', "+
                "friendlyValue='"+ friendlyValue +"'"+
            "]";
    }

}
