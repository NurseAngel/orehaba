package mods.nurseangel.orehaba;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class OrehabaUtils {

	/**
	 * プレイヤーが範囲内にいるかを判定
	 *
	 * @param player
	 * @param blockX
	 * @param blockY
	 * @param blockZ
	 * @param direction
	 * @return
	 */
	public static final boolean isInArea(EntityPlayer player, int blockX, int blockY, int blockZ, int direction) {
		return isInArea(player.worldObj, player.posX, player.posY - player.yOffset, player.posZ, blockX, blockY, blockZ, direction);
	}

	/**
	 * 範囲内にいるかを判定
	 *
	 * @param world
	 * @param playerX
	 *            プレイヤーの位置
	 * @param playerY
	 *            posYは目線の位置なので-yOffsetすること
	 * @param playerZ
	 * @param blockX
	 *            十字架ブロックの位置と方向
	 * @param blockY
	 * @param blockZ
	 * @param direction
	 * @return
	 */
	public static final boolean isInArea(World world, double playerX, double playerY, double playerZ, int blockX, int blockY, int blockZ, int direction) {

		// 幅3ブロック内であればOK
		if (isInAreaDirection(playerX, playerY, playerZ, blockX, blockY, blockZ, direction)) {
			return true;
		}

		// 幅3ブロック外だった場合、今立っているブロックがダイヤブロックか黒曜石であればOKとする
		int x = MathHelper.floor_double(playerX);
		int y = MathHelper.floor_double(playerY);
		int z = MathHelper.floor_double(playerZ);

		/**
		 * TODO 今立っているブロックを取得したい。<br />
		 * 上記xzは現在居る場所なので、スニークではみ出るとはみ出た先のブロックになる。
		 *
		 * 現在はとりあえず<br />
		 * 足下9ブロックの真下岩盤までのうちいずれかにダイヤブロックがあればいい、とする。
		 */
		int blockID = 0;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int k = z - 1; k <= z + 1; k++) {
				for (int j = y; j > 1; j--) {
					blockID = world.getBlockId(i, j, k);
					// FMLLog.info("blockID:" + blockID + "  x:" + i + "  y:" +
					// j + "  z:" + k);
					if (blockID == Block.blockDiamond.blockID) {
						return true;
					}
					// 黒曜石であれば、周囲3ブロック内にダイヤがあるか探す
					if ((blockID == Block.obsidian.blockID) && (x == i) && (z == k)) {
						if (isFoundDiamond(world, x, j, z, blockX, blockY, blockZ, direction)) {
							return true;
						}
					}

				}
			}
		}

		// 範囲外
		return false;
	}

	/**
	 * 同じ高さの5*5ブロック内にダイヤブロックがあるかを探す<br />
	 * 完全自由スペース用
	 *
	 * @param playerX
	 * @param playerY
	 * @param playerZ
	 * @param blockX
	 *            十字架の位置と方向。使わない
	 * @param blockY
	 * @param blockZ
	 * @param direction
	 * @return
	 */
	public static boolean isFoundDiamond(World world, double playerX, double playerY, double playerZ, int blockX, int blockY, int blockZ, int direction) {

		int x = MathHelper.floor_double(playerX);
		int y = MathHelper.floor_double(playerY);
		int z = MathHelper.floor_double(playerZ);

		/**
		 * 完全自由スペースの場合、真下と周囲3*3にダイヤがない可能性がある。<br />
		 * そのため周囲5*5まで探索する。<br />
		 * これだと実はダイヤ-黒曜石*4-ダイヤの道で外に行けてしまうのだが、まあそれくらいはいいでしょう。
		 */

		int blockID;
		for (int i = x - 2; i <= x + 2; i++) {
			for (int k = z - 2; k <= z + 2; k++) {
				blockID = world.getBlockId(i, y, k);
				if (blockID == Block.blockDiamond.blockID) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 対象が範囲内であるかを判定。対象のみ
	 *
	 * @param playerX
	 * @param playerY
	 * @param playerZ
	 * @param blockX
	 * @param blockY
	 * @param blockZ
	 * @param direction
	 * @return
	 */
	public static final boolean isInAreaDirection(double playerX, double playerY, double playerZ, int blockX, int blockY, int blockZ, int direction) {
		switch (direction) {
		case 0:
			// Xは+-1、zは+方向無限
			if ((int) playerZ > blockZ) {
				if ((Math.abs(blockX + 0.5 - playerX)) < 1.8) {
					return true;
				}
			}
			break;
		case 2:
			// Xは+-1、Zは-無限
			if ((int) playerZ < blockZ) {
				if ((Math.abs(blockX + 0.5 - playerX)) < 1.8) {
					return true;
				}
			}
			break;
		case 1:
			// Zは+-1、Xは-無限
			if ((int) playerX < blockX) {
				if ((Math.abs(blockZ + 0.5 - playerZ)) < 1.8) {
					return true;
				}
			}
			break;
		case 3:
			// Zは+-1、Xは+無限
			if ((int) playerX > blockX) {
				if ((Math.abs(blockZ + 0.5 - playerZ)) < 1.8) {
					return true;
				}
			}
			break;
		}
		return false;
	}
}
