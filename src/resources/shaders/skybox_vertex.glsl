#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 TexCoords;
out vec3 FragPos;

uniform mat4 projection;
uniform mat4 view;



void main()
{
    //TexCoords = aPos;
    TexCoords = vec3(aPos.x, -aPos.y, aPos.z); // Flip X so that words make sense again
    vec4 pos = projection * view * vec4(aPos, 1.0);
    FragPos = vec3(pos);
    gl_Position = pos.xyww;
}