/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * Flower pot block.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class FlowerPotModel {
	private static final AABB[] boxes = {
		// east
		new AABB(10/16., 11/16., 0, 6/16., 5/16., 11/16.),
		// west
		new AABB(5/16., 6/16., 0, 6/16., 5/16., 11/16.),
		// north
		new AABB(5/16., 11/16., 0, 6/16., 5/16., 6/16.),
		// south
		new AABB(5/16., 11/16., 0, 6/16., 10/16., 11/16.),
		// center
		new AABB(6/16., 10/16., 0, 4/16., 6/16., 10/16.),
	};
	
	private static final AABB cactus =
		new AABB(6/16., 10/16., 4/16., 1, 6/16., 10/16.);
	
	protected static Quad[] flower = {
		new DoubleSidedQuad(new Vector3d(0, 4/16., 0), new Vector3d(1, 4/16., 1),
					new Vector3d(0, 1, 0), new Vector4d(0, 1, 0, 12/16.)),
					
		new DoubleSidedQuad(new Vector3d(1, 4/16., 0), new Vector3d(0, 4/16., 1),
				new Vector3d(1, 1, 0), new Vector4d(0, 1, 0, 12/16.)),
	};
	
	private static final Texture[] tex = {
		Texture.flowerPot,
		Texture.flowerPot,
		Texture.flowerPot,
		Texture.flowerPot,
		Texture.dirt,
	};
	
	/**
	 * Find intersection between ray and block
	 * @param ray
	 * @return <code>true</code> if the ray intersected the block
	 */
	public static boolean intersect(Ray ray) {
		boolean hit = false;
		int flower = ray.getBlockData();
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < boxes.length; ++i) {
			if (boxes[i].intersect(ray)) {
				tex[i].getColor(ray);
				ray.t = ray.tNear;
				hit = true;
			}
		}
		switch (flower) {
		case 0:
		default:
			break;
		case 1:
			hit |= flower(ray, Texture.redRose);
			break;
		case 2:
			hit |= flower(ray, Texture.yellowFlower);
			break;
		case 3:
			hit |= flower(ray, Texture.oakSapling);
			break;
		case 4:
			hit |= flower(ray, Texture.spruceSapling);
			break;
		case 5:
			hit |= flower(ray, Texture.birchSapling);
			break;
		case 6:
			hit |= flower(ray, Texture.jungleTreeSapling);
			break;
		case 7:
			hit |= flower(ray, Texture.redMushroom);
			break;
		case 8:
			hit |= flower(ray, Texture.brownMushroom);
			break;
		case 9:
			hit |= cactus(ray);
			break;
		case 10:
			hit |= flower(ray, Texture.deadBush);
			break;
		case 11:
			hit |= flower(ray, Texture.fern);
			break;
		}
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
		}
		return hit;
	}

	private static boolean flower(Ray ray, Texture texture) {
		boolean hit = false;
		for (Quad quad : flower) {
			if (quad.intersect(ray)) {
				float[] color = texture.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.t = ray.tNear;
					ray.n.set(quad.n);
					ray.n.scale(-Math.signum(ray.d.dot(quad.n)));
					hit = true;
				}
			}
		}
		return hit;
	}

	private static boolean cactus(Ray ray) {
		if (cactus.intersect(ray)) {
			if (ray.n.y > 0) {
				Texture.cactusTop.getColor(ray);
			} else {
				Texture.cactusSide.getColor(ray);
			}
			ray.color.w = 1;
			ray.t = ray.tNear;
			return true;
		}
		return false;
	}
}
