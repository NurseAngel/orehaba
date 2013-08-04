package mods.nurseangel.orehaba;

import java.util.logging.Level;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Config {

	/** コンフィグファイル */
	private Configuration cfg;

	/**
	 * コンストラクタ
	 *
	 * @param cfg
	 * @return
	 */
	public Config(FMLPreInitializationEvent event) {
		cfg = new Configuration(event.getSuggestedConfigurationFile());
		readConfig();
	}

	/** 十字架ブロックID */
	public static int BlockIdJuujikachan;
	/** 十字架レシピを有効化するか */
	public static boolean enableRecipie = true;
	/** テストフラグ */
	public static boolean isTest = false;
	/** 範囲外に出たときのメッセージ */
	public static String messageOuaArea = "§4 あなたは世界の理から足を踏み外してしまった!";
	/** 範囲内に戻ったときのメッセージ */
	public static String messageReturnArea = "世界に秩序が戻った";
	/** ブロックを設置したときのメッセージ */
	public static String messageSetBlock = "§3 世界への軛があまねく公布された";

	/** 初めて出たときに鳴る音 */
	public static String playSoundFirst = "random.classic_hurt";
	/** 外に出続けているときに鳴る音 */
	public static String playSound = "random.classic_hurt";

	private static final String categoryName = "juujikachan";

	/**
	 * コンフィグファイルから読み込み
	 *
	 * @param cfg
	 */
	private void readConfig() {

		int blockIdStart = 1400;
		String enableComment = "if you want no more juujikachan, set \"false\"";
		String messageReturnAreaComment = "display message";
		String messageOuaAreaComment = "if you do'nt want to display message, set \"\"";
		String messageSetBlockComment = "@see http://www.minecraftwiki.net/wiki/Formatting_codes";
		String playSoundFirstComment = "if you do'nt want to play sound, set \"\"";
		String playSoundComment = "see assets/sound. default \"random.classic_hurt\" ";

		try {
			cfg.load();
			BlockIdJuujikachan = cfg.getBlock("juujikachan", blockIdStart).getInt();
			enableRecipie = cfg.get(Configuration.CATEGORY_GENERAL, "enableRecipie", true, enableComment).getBoolean(true);
			isTest = cfg.get(Configuration.CATEGORY_GENERAL, "isTest", false, "Always false").getBoolean(false);

			messageReturnArea = cfg.get(Configuration.CATEGORY_GENERAL, "messageReturnArea", messageReturnArea, messageReturnAreaComment).getString();
			messageOuaArea = cfg.get(Configuration.CATEGORY_GENERAL, "messageOuaArea", messageOuaArea, messageOuaAreaComment).getString();
			messageSetBlock = cfg.get(Configuration.CATEGORY_GENERAL, "messageSetBlock", messageSetBlock, messageSetBlockComment).getString();

			playSoundFirst = cfg.get(Configuration.CATEGORY_GENERAL, "playSoundFirst", playSoundFirst, playSoundFirstComment).getString();
			playSound = cfg.get(Configuration.CATEGORY_GENERAL, "playSound", playSound, playSoundComment).getString();

		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, Reference.MOD_NAME + " configuration loadding failed");
		} finally {
			cfg.save();
		}
	}

	/**
	 * 引数の項目を取得
	 *
	 * @param key
	 *            コンフィグのキー名
	 * @return int[] x,y,z,direction
	 *
	 */
	public int[] getWorldConfig(String key) {
		try {
			cfg.load();
			String orehaba = cfg.get(categoryName, key, "").getString();
			if (orehaba == null || orehaba.equals("")) {
				return null;
			}

			// :で分割
			String[] value = orehaba.split(":");

			int[] inte = { Integer.parseInt(value[0]), Integer.parseInt(value[1]), Integer.parseInt(value[2]), Integer.parseInt(value[3]) };
			return inte;

		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, Reference.MOD_NAME + " getWorldConfig " + key + " failed");
		} finally {
			/**
			 * セーブはしない<br />
			 * TODO デフォルトを設定しない、単に読み込むだけで元を汚染しないgetはないんじゃろうか?
			 */
			cfg.load();
		}
		return null;
	}

	/**
	 * 引数の項目を保存
	 *
	 * @param key
	 *            コンフィグのキー名
	 * @param x
	 * @param y
	 * @param z
	 * @param direction
	 */
	public void saveWorldConfig(String key, int x, int y, int z, int direction) {
		/**
		 * TODO これでセーブすると何故かCATEGORY_GENERALのコメントが消える。
		 */

		// Stringに
		String value = "" + x + ":" + y + ":" + z + ":" + direction;

		try {
			cfg.load();

			Property prop = new Property(key, value, Property.Type.STRING);
			cfg.getCategory(categoryName).put(key, prop);

		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, Reference.MOD_NAME + " saveWorldCoionfig " + key + " failed");
		} finally {
			cfg.save();
		}

	}

}
