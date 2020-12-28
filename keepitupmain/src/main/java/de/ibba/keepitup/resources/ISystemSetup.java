package de.ibba.keepitup.resources;

public interface ISystemSetup {

    SystemSetupResult exportData();

    SystemSetupResult importData(String data);
}
