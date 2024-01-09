#version 330 core
out vec4 FragColor;


in vec3 TexCoords;
in vec3 FragPos;

uniform samplerCube skybox;
uniform vec3 plyPos;

vec3 lightColor = vec3(1.0, 0.95, 0.8);

void main()
{
    vec4 skyColor = texture(skybox, TexCoords);
    FragColor = skyColor;
}
