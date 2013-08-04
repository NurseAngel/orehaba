package mods.nurseangel.orehaba;

import mods.nurseangel.orehaba.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class ModOrehaba {

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	private Config config;

	public static OrehabaCrossBlock orehabaBlock;

	/**
	 * コンストラクタ的なもの
	 *
	 * @param event
	 */
	@Mod.EventHandler
	public void myPreInitMethod(FMLPreInitializationEvent event) {
		config = new Config(event);
	}

	/**
	 * load()なもの
	 *
	 * @param event
	 */
	@Mod.EventHandler
	public void myInitMethod(FMLInitializationEvent event) {

		// ブロック
		orehabaBlock = new OrehabaCrossBlock(config.BlockIdJuujikachan);
		orehabaBlock.setUnlocalizedName("orehabaCrossBlock").func_111022_d("dirt");
		LanguageRegistry.addName(orehabaBlock, "十字架ちゃん");
		GameRegistry.registerBlock(orehabaBlock, "orehabaCrossBlock");

		if (config.enableRecipie) {
			GameRegistry.addRecipe(new ItemStack(orehabaBlock, 1), new Object[] { " X ", "XXX", " X ", 'X', Block.dirt });
		}

		// ハンドラコントローラ起動
		proxy.registerHandler(config);
	}

}