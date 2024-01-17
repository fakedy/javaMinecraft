public class primitives {


    static class faces {


        // NOT USED
                /*
                    verts = new float[]{
                            // Front face
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-right
                            0.5f + x, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            0.5f + x, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            -0.5f + x, 0.35f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-left

                    };

                    verts = new float[]{
                            // Left face
                            -0.5f + x, 0.35f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right
                            -0.5f + x, 0.35f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // top-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // bottom-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-right
                            -0.5f + x, 0.35f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right

                    };

                    verts = new float[]{
                            // Right face
                            0.5f + x, 0.35f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f,  // bottom-right
                            0.5f + x, 0.35f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // bottom-right
                            0.5f + x, 0.35f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f,// bottom-left

                    };

                            // Back face
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,  // Bottom-left
                            0.5f + x, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// bottom-right
                            0.5f + x, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// top-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // bottom-left
                            -0.5f + x, 0.35f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-left

                    };

                            // Bottom face
                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,   // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,  // top-right

                    };

                 */


        /*
                    verts = new float[]{
                            // Front face
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-right
                            0.5f + x, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            0.5f + x, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-right
                            -0.5f + x, 0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// top-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, 0.0f, 1.0f,// bottom-left

                    };

                    verts = new float[]{
                            // Left face
                            -0.5f + x, 0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right
                            -0.5f + x, 0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // top-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f, // bottom-left
                            -0.5f + x, -0.5f + y, -0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f,// bottom-right
                            -0.5f + x, 0.5f + y, 0.5f + z, -1.0f, 0.0f, 0.0f, // top-right

                    };

                    verts = new float[]{
                            0.5f + x, 0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f,  // bottom-right
                            0.5f + x, 0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 1.0f, 0.0f, 0.0f, // bottom-right
                            0.5f + x, 0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 1.0f, 0.0f, 0.0f,// bottom-left

                    };

                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,  // Bottom-left
                            0.5f + x, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// bottom-right
                            0.5f + x, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f,// top-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // bottom-left
                            -0.5f + x, 0.5f + y, -0.5f + z, 0.0f, 0.0f, -1.0f, // top-left

                    };

                    verts = new float[]{
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,   // top-right
                            0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f, // top-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-left
                            0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f,// bottom-left
                            -0.5f + x, -0.5f + y, 0.5f + z, 0.0f, -1.0f, 0.0f, // bottom-right
                            -0.5f + x, -0.5f + y, -0.5f + z, 0.0f, -1.0f, 0.0f,  // top-right

                    };



    }

         */

    }
}
