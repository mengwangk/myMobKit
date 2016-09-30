package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;
import com.mymobkit.common.StringUtils;

public final class DeviceNetworkInfo {
	
	public enum ConnectionType {
		WIFI,
		MOBILE
	}

	public enum PhoneType {
		NONE,
		GSM,
		CDMA,
		SIP
	}
	
	public enum SimState {
		UNKNOWN,
		ABSENT,
		PIN_REQUIRED,
		PUK_REQUIRED,
		NETWORK_LOCKED,
		READY,
		ERROR,
	}
	
	public enum CellLocationType {
		GSM,
		CDMA
	}
	
	@Expose
	private boolean isConnected;
	
	@Expose
	private ConnectionType connectionType;
	
	@Expose
	private String phoneNumber;
	
	@Expose
	private String ipAddress;
	
	@Expose
	private boolean isNetworkRoaming;
	
	@Expose
	private String deviceId;
	
	@Expose
	private String deviceSoftwareVersion;
	
	@Expose
	private String networkCountryIso;
	
	@Expose
	private String networkOperator;
	
	@Expose
	private String networkOperatorName;
	
	@Expose
	private String networkType;
	
	@Expose
	private String subscriberId;
	
	@Expose
	private String voiceMailNumber;
	
	@Expose
	private PhoneType phoneType;
	
	@Expose
	private SimState simState;
	
	@Expose
	private String simCountryIso;
	
	@Expose
	private String simOperator;
	
	@Expose
	private String simOperatorName;
	
	@Expose
	private String simSerialNumber;
	
	@Expose
	private CellLocationType cellLocationType;
	
	@Expose
	private DeviceGsmCellLocation gsmCellLocation;
	
	@Expose
	private DeviceCdmaCellLocation cdmaCellLocation;
	
	@Expose
	private DeviceSignalStrength signalStrength;
	
	/**
	 * Default constructor.
	 * 
	 */
	public DeviceNetworkInfo(){
		phoneNumber = StringUtils.EMPTY;
		isConnected = false;
		ipAddress = StringUtils.EMPTY;
		connectionType = ConnectionType.WIFI;
		isNetworkRoaming = false;
		networkCountryIso = StringUtils.EMPTY;
		networkOperator = StringUtils.EMPTY;
		networkOperatorName = StringUtils.EMPTY;
		networkType = StringUtils.EMPTY;
		deviceId = StringUtils.EMPTY;
		deviceSoftwareVersion = StringUtils.EMPTY;
		deviceId  = StringUtils.EMPTY;
		subscriberId = StringUtils.EMPTY;
		voiceMailNumber = StringUtils.EMPTY;
		simState = SimState.UNKNOWN;
		simCountryIso = StringUtils.EMPTY;
		simOperator = StringUtils.EMPTY;
		simOperatorName = StringUtils.EMPTY;
		simSerialNumber = StringUtils.EMPTY;
		cellLocationType = CellLocationType.GSM;
	}


	public String getNetworkType() {
		return networkType;
	}


	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public boolean isConnected() {
		return isConnected;
	}


	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}


	public ConnectionType getConnectionType() {
		return connectionType;
	}


	public void setConnectionType(ConnectionType connectionType) {
		this.connectionType = connectionType;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public boolean isNetworkRoaming() {
		return isNetworkRoaming;
	}


	public void setNetworkRoaming(boolean isNetworkRoaming) {
		this.isNetworkRoaming = isNetworkRoaming;
	}


	public String getNetworkCountryIso() {
		return networkCountryIso;
	}


	public void setNetworkCountryIso(String networkCountryIso) {
		this.networkCountryIso = networkCountryIso;
	}


	public String getNetworkOperator() {
		return networkOperator;
	}


	public void setNetworkOperator(String networkOperator) {
		this.networkOperator = networkOperator;
	}


	public String getNetworkOperatorName() {
		return networkOperatorName;
	}


	public void setNetworkOperatorName(String networkOperatorName) {
		this.networkOperatorName = networkOperatorName;
	}


	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	public String getDeviceSoftwareVersion() {
		return deviceSoftwareVersion;
	}


	public void setDeviceSoftwareVersion(String deviceSoftwareVersion) {
		this.deviceSoftwareVersion = deviceSoftwareVersion;
	}

	public String getSubscriberId() {
		return subscriberId;
	}


	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}


	public String getVoiceMailNumber() {
		return voiceMailNumber;
	}


	public void setVoiceMailNumber(String voiceMailNumber) {
		this.voiceMailNumber = voiceMailNumber;
	}


	public PhoneType getPhoneType() {
		return phoneType;
	}


	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}


	public String getSimCountryIso() {
		return simCountryIso;
	}


	public void setSimCountryIso(String simCountryIso) {
		this.simCountryIso = simCountryIso;
	}


	public String getSimOperator() {
		return simOperator;
	}


	public void setSimOperator(String simOperator) {
		this.simOperator = simOperator;
	}


	public String getSimOperatorName() {
		return simOperatorName;
	}


	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}


	public String getSimSerialNumber() {
		return simSerialNumber;
	}


	public void setSimSerialNumber(String simSerialNumber) {
		this.simSerialNumber = simSerialNumber;
	}


	public SimState getSimState() {
		return simState;
	}


	public void setSimState(SimState simState) {
		this.simState = simState;
	}


	public CellLocationType getCellLocationType() {
		return cellLocationType;
	}


	public void setCellLocationType(CellLocationType cellLocationType) {
		this.cellLocationType = cellLocationType;
	}


	public DeviceGsmCellLocation getGsmCellLocation() {
		return gsmCellLocation;
	}


	public void setGsmCellLocation(DeviceGsmCellLocation gsmCellLocation) {
		this.gsmCellLocation = gsmCellLocation;
	}


	public DeviceCdmaCellLocation getCdmaCellLocation() {
		return cdmaCellLocation;
	}


	public void setCdmaCellLocation(DeviceCdmaCellLocation cdmaCellLocation) {
		this.cdmaCellLocation = cdmaCellLocation;
	}


	public DeviceSignalStrength getSignalStrength() {
		return signalStrength;
	}


	public void setSignalStrength(DeviceSignalStrength signalStrength) {
		this.signalStrength = signalStrength;
	}
	
}
