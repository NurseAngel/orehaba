package mods.nurseangel.orehaba.proxy;

import mods.nurseangel.orehaba.Config;
import mods.nurseangel.orehaba.handler.HandlerController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void registerHandler(Config config) {
		// ハンドラコントローラ起動
		HandlerController.getInstance().setConfig(config).registerHandler(Side.CLIENT);
		HandlerController.getInstance().setConfig(config).registerHandler(Side.SERVER);
	}
}