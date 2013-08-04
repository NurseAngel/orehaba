package mods.nurseangel.orehaba.handler;

import java.util.EnumSet;

import mods.nurseangel.orehaba.Config;
import mods.nurseangel.orehaba.OrehabaUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

/**
 * 俺幅ハンドラ<br />
 * tickごとに呼ばれる大変忙しいハンドラ<br />
 * もっと適任っぽいのが有ると思うんだけどよくわからん
 *
 * {@link OrehabaTickClientHandler}
 *
 */
public class OrehabaTickHandler implements IScheduledTickHandler {
	private final String TAG = "OrehabaTickHandler";

	/** コンフィグファイル */
	private Config config;
	/** 呼び出し間隔 */
	private int nextTickSpacing = 20;

	/**
	 * ハンドラのロック<br />
	 * ロック中は何もしない。<br />
	 * これだけ名前的にtrueだと止まる。
	 *
	 * コンフィグ更新時などにロックするために使用<br />
	 * だがマルチスレッド苦手なので実はまともに機能してないんじゃないか疑惑
	 *
	 * 最優先
	 */
	private static boolean isLocked = false;

	/**
	 * 現在の世界のコンフィグが存在するかのフラグ<br />
	 * コンフィグがなければ動作しない。<br />
	 *
	 * ログイン/ゲート移動などで状態変更になる。
	 *
	 * 二番目
	 */
	private static boolean isConfigExists = false;

	/**
	 * 現在ハンドラが有効か否かのフラグ<br />
	 * 死亡などで圏外に出たときに一時的に外れる用に使う。
	 *
	 * 無効であればtick毎に有効化確認を行う
	 *
	 * 三番目
	 */
	private static boolean isEnabled = false;

	/**
	 * 現在圏内に居る場合にtrue、圏外に出たらfalse<br />
	 */
	private boolean isInArea = false;

	/** 有効範囲のx、z、方向。yは使わない */
	static int blockX;
	static int blockZ;
	static int direction;

	/**
	 * コンストラクタ
	 *
	 * @param config
	 */
	public OrehabaTickHandler(Config config) {
		this.config = config;
	}

	/**
	 * tickHandler以外で、現在ハンドラの実行をしてよいかを取得
	 */
	public static final boolean isInEffective() {

		if (isLocked || !isConfigExists) {
			return false;
		}

		return isEnabled;
	}

	/**
	 * tick開始時に呼ばれる
	 */
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

		// 何もしない
		if (isLocked || !isConfigExists) {
			return;
		}

		// ヘルプには0がプレイヤー、1がWorldと書いてあるけど実際は0しか無いみたい
		EntityPlayer player = (EntityPlayer) tickData[0];

		// 有効/無効で分岐
		if (isEnabled) {
			tickInEffective(player);
		} else {
			tickNotEffective(player);
		}
	}

	/**
	 * ハンドラが有効なtickに呼ばれる
	 */
	private void tickInEffective(EntityPlayer player) {

		// 現在位置が圏内である/ダイヤブロックであればOKなので特に何もしない
		if (isInArea(player)) {

			if (isInArea == false) {
				if (player.worldObj.isRemote && config.messageReturnArea.length() > 1) {
					player.addChatMessage(config.messageReturnArea);
				}
			}

			isInArea = true;
			return;
		}

		// 外に出ている状態

		// 装備含め全アイテムが消滅
		player.inventory.clearInventory(-1, -1);

		if (isInArea) {
			// 初めて外に出た場合
			isInArea = false;

			// チャットを表示
			if (player.worldObj.isRemote && config.messageOuaArea.length() > 1) {
				player.addChatMessage(config.messageOuaArea);
			}

			// 音
			if (config.playSoundFirst.length() > 1) {
				player.worldObj.playSoundAtEntity(player, config.playSoundFirst, 1.0F, 1.0F);
			}

		} else {
			// 続けて出ている

			int hoge = player.getFoodStats().getFoodLevel();
			if (hoge >= 0) {
				// おなかが減る
				player.getFoodStats().setFoodLevel(hoge - 1);
				// 音
				if (config.playSound.length() > 1) {
					player.worldObj.playSoundAtEntity(player, config.playSound, 1.0F, 1.0F);
				}

			} else {
				// ダメージ
				player.attackEntityFrom(DamageSource.outOfWorld, 1.0F);
				// 音はダメージの時に出る
			}
		}

	}

	/**
	 * ハンドラが無効なtickに呼ばれる<br />
	 * ゲーム開始直後、リスポーン直後など。
	 */
	private void tickNotEffective(EntityPlayer player) {

		// 圏内に入れば
		if (isInArea(player)) {
			// ハンドラを有効化、圏内フラグも立てる
			enableTickHandler(true);
			isInArea = true;

		}

		// 他には特になし
		return;

	}

	/**
	 * Worldを更新した<br />
	 * ログイン、ディメンション移動など
	 *
	 * @param world
	 */
	public void updateWorld(World world) {

		// ロックする
		isLocked = true;

		// コンフィグのキー名を作成
		String key = getConfigKey(world);

		// 該当のコンフィグを取得
		int[] worldConfig = config.getWorldConfig(key);

		if (worldConfig == null) {
			// コンフィグがなかった
			isConfigExists = false;
		} else {
			// コンフィグがあれば、その設定で更新する
			updateOrehaba(worldConfig[0], 0, worldConfig[2], worldConfig[3]);
			isConfigExists = true;
		}

		// ロック解除
		isLocked = false;
	}

	/**
	 * 一時的な有効/無効状態を切り替える
	 *
	 * @param b
	 */
	public void enableTickHandler(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * 世界にブロックが置かれた
	 *
	 * @param world
	 * @param x
	 * @param y
	 *            上下は使わない
	 * @param z
	 * @param direction
	 *            生存圏の方向
	 */
	public void onBlockPlacedByPlayer(World world, int x, int y, int z, int direction) {

		// ロックする
		isLocked = true;

		// コンフィグのキー名を作成
		String key = getConfigKey(world);

		// 現在のコンフィグを上書き保存(無ければ項目新設)
		config.saveWorldConfig(key, x, y, z, direction);

		// 有効範囲を更新する
		updateOrehaba(x, y, z, direction);

		// チャットを表示

		if (world.isRemote && config.messageSetBlock.length() > 1) {
			world.getClosestPlayer(x, y, z, -1F).addChatMessage(config.messageSetBlock);
		}

		// ロック解除
		isConfigExists = true;
		isLocked = false;
	}

	/**
	 * 俺幅範囲を更新
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param direction
	 */
	public void updateOrehaba(int x, int y, int z, int direction) {
		// 範囲のみ。条件は特に変更しない
		this.blockX = x;
		this.blockZ = z;
		this.direction = direction;
	}

	/**
	 * コンフィグのキー名を決定する
	 *
	 * @param world
	 * @return
	 */
	public String getConfigKey(World world) {

		// ディレクトリ名+ディメンションIDでたぶん一意になるはず
		String dirname = FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName();
		String key = dirname + world.provider.dimensionId;

		return key;
	}

	/**
	 * 範囲内にいるかを判定
	 *
	 * @param player
	 * @return 範囲内であればtrue
	 */
	public static boolean isInArea(EntityPlayer player) {
		return OrehabaUtils.isInArea(player, blockX, 0, blockZ, direction);

	}

	// --------------------------------------------------------------------------------------------------
	// 以下はOverrideしてるだけ

	/**
	 * 次のtickまでの間隔
	 */
	@Override
	public int nextTickSpacing() {
		return nextTickSpacing;
	}

	/**
	 * tick終了時に呼ばれる
	 */
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	/**
	 * どの種別のハンドラを受け取るか
	 */
	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	/**
	 * ハンドラの固有名でいいのかな?
	 */
	@Override
	public String getLabel() {
		return TAG;
	}

}
