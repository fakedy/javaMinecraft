#version 330 core
out vec4 FragColor;
uniform samplerCube skybox;
in vec3 TexCoords;

void main()
{
    vec4 skyColor = texture(skybox, TexCoords);
    float fogFactor = 0.6; // Maximum fog for skybox
    vec3 fogColor = vec3(47/255, 187/255, 12/255);
    FragColor = mix(skyColor, vec4(fogColor, 1.0), fogFactor);
}
