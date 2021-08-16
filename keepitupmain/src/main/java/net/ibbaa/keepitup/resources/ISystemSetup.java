package net.ibbaa.keepitup.resources;

public interface ISystemSetup {

    SystemSetupResult exportData();

    SystemSetupResult importData(String data);
}
