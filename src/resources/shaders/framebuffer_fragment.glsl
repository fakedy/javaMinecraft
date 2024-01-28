#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D screenTexture;
uniform sampler2D depthTexture;

vec3 lightDirection = vec3(-0.5f, -1.0f, -0.5f);

float near = 0.1;
float far  = 256.0;


float LinearizeDepth(float depth)
{
    float z = depth * 2.0 - 1.0; // back to NDC
    return (2.0 * near * far) / (far + near - z * (far - near));
}

void main()
{
    float depthR = texture(depthTexture, TexCoords).r;
    float depthLinear = LinearizeDepth(depthR)/far;
    FragColor = texture(screenTexture, TexCoords);
}