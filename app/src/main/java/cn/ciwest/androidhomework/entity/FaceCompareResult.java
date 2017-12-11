package cn.ciwest.androidhomework.entity;

public class FaceCompareResult {
	private float similar;

	public float getSimilar() {
		return similar;
	}

	public void setSimilar(float similar) {
		this.similar = similar;
	}

//	@Override
//	public String toString() {
//		return "FaceDetectResult [similar=" + similar + "]";
//	}
	@Override
	public String toString() {
		return "相似度：" + similar*100 + "%";
	}

}
