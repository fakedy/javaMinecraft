#version 330 core
out vec4 FragColor;


in vec3 TexCoords;
in vec3 FragPos;

uniform samplerCube skybox;
uniform vec3 plyPos;

vec3 fogColor = vec3(0.0, 0.25, 0.25);



void main()
{


    //vec4 skyColor = texture(skybox, TexCoords);
    vec4 skyColor = vec4(fogColor, 1.0);
    FragColor = skyColor;
}
