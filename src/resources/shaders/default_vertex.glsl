#version 460 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec3 aTexCoord;

out vec3 TexCoord;
out vec3 Normal;
out vec3 FragPos;
out vec4 FragPosLightSpace;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightSpaceMatrix;
uniform vec3 plyPos;

void main()
{
    gl_Position = projection * view * model * vec4(aPos - plyPos, 1.0);
    FragPos = vec3(model * vec4(aPos- plyPos, 1.0));
    FragPosLightSpace = lightSpaceMatrix * vec4(FragPos, 1.0);
    TexCoord = aTexCoord;
    Normal = transpose(inverse(mat3(model))) * aNormal;
}