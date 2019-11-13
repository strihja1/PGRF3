package lvl2advanced.p01gui.p01simple;

import lwjglutils.OGLBuffers;

public class GridFactory {

    public static OGLBuffers generateGrid(int m, int n){
        float[] vb = new float[m*n*2];


        int index = 0;

        for(int j = 0; j < n; j++){
            for(int i = 0; i < m; i++){
                vb[index++] = i / (float) (m-1);
                vb[index++] = j / (float) (n-1);
            }
        }




        int index2 = 0;
        int[] ib = new int[(m-1)*(n-1)*2*3];
        for (int j = 0; j<n-1;j++) {
            int row = j*m;
            for (int i = 0; i < m - 1; i++) {
                ib[index2++] = (row+i);
                ib[index2++] = (row+i+1);
                ib[index2++] = (row+i+m);;


                ib[index2++] = (row+i+m);
                ib[index2++] = (row+i+1);
                ib[index2++] = (row+i+m+1);
            }
        }


        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 2)
        };
        return new OGLBuffers(vb, attribs, ib);
    }

    public static OGLBuffers generateGridTriangleStrip(int m, int n){
        float[] vb = new float[m*n*2];


        int index = 0;

        for(int j = 0; j < n; j++){
            for(int i = 0; i < m; i++){
                vb[index++] = j / (float) (m-1);
                vb[index++] = i / (float) (n-1);
            }
        }




        int index2 = 0;
        int[] ib = new int[(m+1)*(n-1)*2];
        for (int j = 0; j<m-1;j++) {
            for (int i = 0; i <= n; i++) {
                if(j % 2 == 0) {
                    if (i == n) {
                        ib[index2++] = (i - 1) + (j + 1) * n;
                        ib[index2++] = (i - 1) + (j + 1) * n;
                    } else {
                        ib[index2++] = i + j * n;
                        ib[index2++] = i + (j + 1) * n;
                    }
                }else{
                    if (i == n){
                        ib[index2++] = n - i + (j+1)*n;
                        ib[index2++] = n - i + (j+1)*n;
                    }else{
                        ib[index2++] = (n-1) - i + (j+1)*n;
                        ib[index2++] = j*n + (n-1) - i;
                    }
                }
            }
        }


        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 2)
        };
        return new OGLBuffers(vb, attribs, ib);
    }
  /*  public static OGLBuffers generateGrid(int m, int n){
        float[] vb = new float[m*n*2];


        int index = 0;

        for(int j = 0; j < n; j++){
            for(int i = 0; i < m; i++){
              vb[index++] = i / (float) (m-1);
              vb[index++] = j / (float) (n-1);
            }
        }



        int index2 = 0;
        int[] ib = new int[(m-1)*(n-1)*2*3];
        for (int j = 0; j<n-1;j++) {
            int row = j*m;
            for (int i = 0; i < m - 1; i++) {
                ib[index2++] = (row+i);
                ib[index2++] = (row+i+1);
                ib[index2++] = (row+i+m);


                ib[index2++] = (row+i+m);

                ib[index2++] = (row+i+1);
                ib[index2++] = (row+i+m+1);
                System.out.println("--");
            }
        }

        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 2)
        };
        return new OGLBuffers(vb, attribs, ib);
    }

   */
}
